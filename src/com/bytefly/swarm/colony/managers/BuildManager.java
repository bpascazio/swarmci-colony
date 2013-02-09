package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.builders.AndroidBuilder;
import com.bytefly.swarm.colony.builders.Builder;
import com.bytefly.swarm.colony.builders.XCodeBuilder;
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Debug;

// Work is to build an app and send out.

public class BuildManager extends Manager {
	int fake_count = 0;
	public void run() {
		this.start();
		Debug.Log(Debug.INFO, "BuildManager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "BuildManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE, "BuildManager received item "+w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					//told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_BUILD_BUILD_PROJECT)) {
					// pull code from git here then queue a build
					Project p = (Project) w.data;
					Debug.Log(Debug.DEBUG, "BuildManager executing build for "+p.Name);
					Status.counter_builds_total++;
					if (p.BuilderType == Builder.BUILDER_TYPE_XCODE) {
						Debug.Log(Debug.TRACE, "BuildManager executing xcode project");
						Status.counter_builds_xcode++;
						XCodeBuilder xcb = new XCodeBuilder();
						xcb.p = p;
						xcb.runAll();
					}
					if (p.BuilderType == Builder.BUILDER_TYPE_ANDROID) {
						Debug.Log(Debug.TRACE, "BuildManager executing android project");
						Status.counter_builds_android++;
						AndroidBuilder ab = new AndroidBuilder();
						ab.p = p;
						ab.runAll();
					}
				}				
			} catch (Exception e) {
				Debug.Log(Debug.INFO, "BuildManager work queue exception - exiting "+e.toString());
				stop();
			}
		}
		Debug.Log(Debug.INFO, "BuildManager stopped.");
	}
}
