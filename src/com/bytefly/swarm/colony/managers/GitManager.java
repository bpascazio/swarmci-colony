package com.bytefly.swarm.colony.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.builders.GitChecker;
import com.bytefly.swarm.colony.builders.XCodeBuilder;
import com.bytefly.swarm.colony.collections.ProjectList;
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Debug;

// Work is to receive lists of projects, check for Git Updates and queue Builds.

public class GitManager extends Manager {

	BuildManager bm;
	ProjectList pl;
	HashMap<String, String> mg;

	public GitManager(BuildManager m) {
		bm = m;
		mg = new HashMap();
	}

	class GitRunnable implements Runnable {

		Project p;

		public GitRunnable(Project _p) {
			p = _p;
		}

		public void run() {
			Debug.Log(Debug.DEBUG,
					"GitManager checking for updates project repo " + p.Repo);

			try {
				// Check for git updates with hashmap
				GitChecker gc = new GitChecker();
				gc.p = p;
				gc.runAll();
				Status.counter_git_updates++;

				if (p.triggerBuild) {
					
					Debug.Log(Debug.TRACE, "Build triggered by project.");
					Status.counter_builds_triggered++;
					p.triggerBuild = false;
					Work bw = new Work(Work.WORK_ITEM_BUILD_BUILD_PROJECT);
					bw.data = p;
					bm.put(bw);
				} else if (gc.lastCheckin != null) {
					
					if (mg.containsKey(p.Repo)) {

						// in the repo
						String existingval = (String) mg.get(p.Repo);
						Debug.Log(Debug.TRACE, "GitManager existing key "
								+ existingval);

						// so compare with checker val
						if (gc.lastCheckin.equals(existingval)) {
							Debug.Log(Debug.INFO, "GitManager not changed for "+p.Name);
							p.busy=false;
						} else {
							Debug.Log(Debug.INFO,
									"Change detected kick off build for "+p.Name);
							

							// set the new key val
							mg.remove(p.Repo);
							mg.put(p.Repo, gc.lastCheckin);

							Work bw = new Work(
									Work.WORK_ITEM_BUILD_BUILD_PROJECT);
							bw.data = p;
							bm.put(bw);
						}
					} else {

						// not so add it
						Debug.Log(Debug.TRACE,
								"GitManager not building - not in repo hash yet "
										+ p.Repo);
						mg.put(p.Repo, gc.lastCheckin);
						p.busy=false;
					}
				}

			} catch (Exception e) {
				Debug.Log(Debug.INFO,
						"GitManager exception in worker thread - exiting "+e);
				stop();
				p.busy=false;
			}
		}
	}

	public void run() {
		Debug.Log(Debug.INFO, "GitManager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "GitManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE,
						"GitManager received item " + w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					// told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_UPDATE_PROJECTS)) {
					pl = (ProjectList) w.data;
				} else if (w.name.equals(Work.WORK_ITEM_GIT_SCAN_PROJECTS)) {
					Debug.Log(Debug.DEBUG,
							"GitManager scanning projects on github for changes...");
					// scan all the projects github repos and possibly queue
					// builds
					if (pl != null && pl.cv != null)
						for (int i = 0; i < pl.cv.size(); i++) {
							Project p = (Project) pl.cv.get(i);
							if (p.busy==false) {
								p.busy=true;
								GitRunnable gr = new GitRunnable(p);
								Thread gth = new Thread(gr);
								gth.start();
							}
						}
				}
			} catch (Exception e) {
				Debug.Log(Debug.INFO,
						"GitManager work queue exception - exiting");
				stop();
			}
		}
		Debug.Log(Debug.INFO, "GitManager stopped.");
	}
}
