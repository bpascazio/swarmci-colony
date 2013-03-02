package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.SecurityContext;
import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.builders.AndroidBuilder;
import com.bytefly.swarm.colony.builders.Builder;
import com.bytefly.swarm.colony.builders.XCodeBuilder;
import com.bytefly.swarm.colony.managers.GitManager.GitRunnable;
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;

// Work is to build an app and send out.

class BuildRunnable implements Runnable {

	Project p;
	SecurityContext sc;

	public BuildRunnable(Project _p, SecurityContext _sc) {
		p = _p;
		sc = _sc;
	}

	public void run() {
		try {
			Debug.Log(Debug.DEBUG, "BuildRunnable forked repo " + p.Repo);
			// pull code from git here then queue a build
			Debug.Log(Debug.DEBUG, "BuildRunnable executing build for " + p.Name);
			Status.counter_builds_total++;
			if (p.BuilderType == Builder.BUILDER_TYPE_XCODE) {
				Debug.Log(Debug.TRACE, "BuildRunnable executing xcode project");
				Status.counter_builds_xcode++;
				XCodeBuilder xcb = new XCodeBuilder();
				xcb.p = p;
				xcb.loadSwarmXML();
				xcb.runAll();
			} else if (p.BuilderType == Builder.BUILDER_TYPE_ANDROID) {
				Debug.Log(Debug.TRACE, "BuildRunnable executing android project");
				Status.counter_builds_android++;
				AndroidBuilder ab = new AndroidBuilder();
				ab.p = p;
				ab.loadSwarmXML();
				ab.runAll();
			} else {
				Builder b = new Builder();
				b.p = p;
				b.loadSwarmXML();
				if (b.toFailList.equals(""))b.toFailList=sc.getSecureEmail();
				p.reason="No%20valid%20project%20found.";
				if (b.p.badGit) {
					p.reason="Failed%20clone%20from%20github.";
					p.commit="none";
					p.buildState=0;
				}
				b.sendFailureEmail();
				Status.counter_builds_failure++;
				Debug.Log(Debug.DEBUG,
						"BuildRunnable flagging build failure no project found.");
			}
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "BuildRunnable X " + e.toString());
		}
		p.buildNum++;
		HttpConnector h = new HttpConnector();
		h.updateEntity(p, p.ProjectId);
		p.setBusy(" end build run "+p.Name, false);
	}
}

public class BuildManager extends Manager {
	int fake_count = 0;
	SecurityContext sc;

	public BuildManager(SecurityContext _sc) {
		sc = _sc;
	}
	
	public void run() {
		this.start();
		Debug.Log(Debug.INFO, "BuildManager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "BuildManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE,
						"BuildManager received item " + w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					// told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_BUILD_BUILD_PROJECT)) {
					Project p = (Project) w.data;
					BuildRunnable gr = new BuildRunnable(p,sc);
					Thread gth = new Thread(gr);
					gth.start();
				}
			} catch (Exception e) {
				Debug.Log(
						Debug.INFO,
						"BuildManager work queue exception - exiting "
								+ e.toString());
				stop();
			}
		}
		Debug.Log(Debug.INFO, "BuildManager stopped.");
	}
}
