package com.bytefly.swarm.colony.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.bytefly.swarm.colony.SecurityContext;
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
	SecurityContext sc;

	public GitManager(SecurityContext _sc, BuildManager m) {
		sc = _sc;
		bm = m;
		mg = new HashMap<String, String>();
	}

	class GitRunnable implements Runnable {

		Project p;

		public GitRunnable(Project _p) {
			p = _p;
		}

		public void run() {
			Debug.Log(p.Name, Debug.DEBUG, "GitManager thread running");
			String msg = "gitrun not";
			try {
				// Check for git updates with hashmap
				GitChecker gc = null;
				if (p.buildState > 0) {
					Debug.Log(p.Name, Debug.DEBUG,
							"GitManager checking for updates project repo "
									+ p.Repo);
					gc = new GitChecker();
					gc.p = p;
					gc.runAll();
					if (gc.invalidGit) {
						Work bw = new Work(Work.WORK_ITEM_BUILD_BUILD_PROJECT);
						bw.data = p;
						p.badGit = true;
						bm.put(bw);
						Debug.Log(p.Name, Debug.DEBUG, "GitManager says bad git");
						return;
					} else {
						p.badGit = false;
						gc.p.commit = gc.lastCheckin;
						Debug.Log(p.Name, Debug.DEBUG, "GitManager says no commit change");
						Status.counter_git_updates++;
					}
				}

				// We only reset the build trigger cleared if the build trigger
				// goes to 0.
				// So what could have happened is a failure to update the
				// trigger to 0 on
				// the last build. buildtriggercleared is still 1 luckily. if
				// buildtrigger
				// is 1 at thispoint we can't trust it since we could be sending
				// out multiple
				// builds. the user will have to swarm stop then swarm run to
				// reset this
				// situation.

				if (p.buildTriggerCleared == 1 && p.buildTrigger == 0) {
					p.buildTriggerCleared = 0;
					Debug.Log(p.Name, Debug.TRACE, "Clearing build trigger.");
				}

				// We can trigger if we are not currently in a cleared state and
				// triggered.
				if (p.buildTriggerCleared == 0 && p.buildTrigger == 1) {

					Debug.Log(p.Name, Debug.TRACE, "Build triggered by project.");
					Status.counter_builds_triggered++;
					p.buildTriggerCleared = 1;
					Work bw = new Work(Work.WORK_ITEM_BUILD_BUILD_PROJECT);
					bw.data = p;
					bm.put(bw);
					return;
				} else if (p.buildState > 0 && gc != null
						&& gc.lastCheckin != null) {

					if (mg.containsKey(p.Repo)) {

						// in the repo
						String existingval = (String) mg.get(p.Repo);
						Debug.Log(p.Name, Debug.TRACE, "GitManager existing key "
								+ existingval);

						// so compare with checker val
						if (gc.lastCheckin.equals(existingval)) {
							Debug.Log(p.Name, Debug.INFO, "GitManager not changed for "
									+ p.Name);
							msg = " gr not changed " + p.Name;
						} else {
							Debug.Log(p.Name, Debug.INFO,
									"Change detected kick off build for "
											+ p.Name);

							// set the new key val
							mg.remove(p.Repo);
							mg.put(p.Repo, gc.lastCheckin);

							Work bw = new Work(
									Work.WORK_ITEM_BUILD_BUILD_PROJECT);
							bw.data = p;
							bm.put(bw);
							return;
						}
					} else {

						// not so add it
						Debug.Log(p.Name, Debug.TRACE,
								"GitManager not building - not in repo hash yet "
										+ p.Repo);
						mg.put(p.Repo, gc.lastCheckin);
						msg = " gr not in hash " + p.Name;
					}
				}

			} catch (Exception e) {
				Debug.Log(p.Name, Debug.INFO,
						"GitManager exception in worker thread - exiting " + e);
				stop();
				msg = " gr exception " + p.Name;
			}
			p.setBusy(msg, false);
		}
	}

	ProjectList mergeProjects(Object _pl) {

		ProjectList newList = (ProjectList) _pl;

		Debug.Log(Debug.TRACE, "mergeProject sz is " + newList.cv.size());

		// Nothing returned in the new list, lets stick with what we have.
		if (newList.cv.size() == 0) {
			return pl;
		}

		// Something in new list and we have nothing, lets stick with newlist.
		if (pl == null)
			return newList;

		boolean found = false;

		// Go through all projects in the new list and merge into current list
		for (int i = 0; i < newList.cv.size(); i++) {

			Project pii = (Project) newList.cv.elementAt(i);

			if (pl != null && pl.cv != null) {
				found = false;
				for (int j = 0; j < pl.cv.size(); j++) {
					Project cp = (Project) pl.cv.elementAt(j);
					if (cp.ProjectId == pii.ProjectId) {

						cp.buildState = pii.buildState;
						cp.buildTrigger = pii.buildTrigger;
						if (cp.getBusy() == false) {
							cp.buildNum = pii.buildNum;
						}
						cp.Builder = pii.Builder;
						Debug.Log(Debug.TRACE,
								"mergeProjects existing project found "
										+ pii.ProjectId);
						cp.setBusy("setbusy merge", cp.getBusy());
						found = true;
						break;
					}
				}
				if (!found) {

					// New project add to the list.
					pl.cv.add(pii);
					Debug.Log(Debug.TRACE, "mergeProjects NEW project found "
							+ pii.ProjectId);
				}
			}
		}

		// Loop through projects and remove any old ones
		for (int j = 0; j < pl.cv.size(); j++) {
			Project cp = (Project) pl.cv.elementAt(j);

			// Make sure the project is in the new list
			for (int i = 0; i < newList.cv.size(); i++) {
				Project pii = (Project) newList.cv.elementAt(i);
				if (cp.ProjectId == pii.ProjectId) {
					found = true;
					break;
				}
			}
			if (!found) {
				pl.cv.removeElementAt(j);
				Debug.Log(Debug.TRACE, "mergeProjects REMOVED project "
						+ cp.ProjectId);
				break;
			}
		}

		return pl;
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
					pl = mergeProjects(w.data);
				} else if (w.name.equals(Work.WORK_ITEM_GIT_SCAN_PROJECTS)) {
					Debug.Log(Debug.DEBUG,
							"GitManager scanning projects on github for changes...");
					// scan all the projects github repos and possibly queue
					// builds
					if (pl != null && pl.cv != null)
						for (int i = 0; i < pl.cv.size(); i++) {
							Project p = (Project) pl.cv.get(i);
							boolean buildit = true;
							if (sc.getSecureEmail().equals("swarm@bytefly.com")) {
								// super user so do not build home build
								// projects
								if (p.Builder.equals("colony")) {
									buildit = false;
								}
							} else {
								if (p.Builder.equals("cloud")) {
									buildit = false;
								}
							}
							Debug.Log(Debug.TRACE, "buildit is " + buildit);
							Debug.Log(Debug.TRACE, "builder is " + p.Builder);

							if (buildit) {
								if (p.getBusy() == false) {
									p.setBusy(" setbusy gitrun " + p.Name, true);
									GitRunnable gr = new GitRunnable(p);
									Thread gth = new Thread(gr);
									gth.start();
								} else {
									Debug.Log(Debug.TRACE,
											"GitManager ignoring busy project "
													+ p.Name);
								}
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
