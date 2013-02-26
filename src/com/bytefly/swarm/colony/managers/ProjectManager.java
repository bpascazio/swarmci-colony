package com.bytefly.swarm.colony.managers;

// Work is to receive fetch collection projects and send to GitManager

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.builders.Builder;
import com.bytefly.swarm.colony.collections.ProjectList;

public class ProjectManager extends Manager {

	GitManager gm;

	public ProjectManager(GitManager m) {
		gm = m;
	}

	public void run() {
		Debug.Log(Debug.INFO, "ProjectManager started.");
		ProjectList pl = null;
		while (running) {
			Debug.Log(Debug.TRACE, "ProjectManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE,
						"ProjectManager received item " + w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					// told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_PROJECT_FETCH_PROJECTS)) {
					Debug.Log(Debug.DEBUG,
							"ProjectManager getting project list.");
					pl = new ProjectList();
					processList(pl);

					// It is not uncommon for the server to respond with an
					// error. In this case we do not
					// pass on the empty list of projects. While this does not
					// handle the corner case of
					// the last project being removed from a cloud server... it
					// should be updated to look
					// at a status return value in the future.
					if (pl != null && pl.cv != null && pl.cv.size() != 0) {
						Work nw = new Work(Work.WORK_ITEM_UPDATE_PROJECTS);
						nw.data = pl;
						try {
							Debug.Log(Debug.TRACE,
									"ProjectManager handing to git manager project update.");
							gm.put(nw);
						} catch (Exception e) {
							Debug.Log(Debug.INFO,
									"ProjectManager exception putting work item - stopping.");
							stop();
						}
						if (Status.counter_loaded_projects == 0
								&& pl.cv.size() != 0) {
							Debug.Log(Debug.TRACE,
									"ProjectManager projects initial load ");

							// send command to git manager
							Work gw = new Work(Work.WORK_ITEM_GIT_SCAN_PROJECTS);
							try {
								Debug.Log(Debug.TRACE,
										"ProjectManager handing to git manager scan projects.");
								gm.put(gw);
							} catch (Exception e) {
								Debug.Log(Debug.INFO,
										"ProjectManager exception putting work item - stopping.");
								stop();
							}
						}
						Status.counter_loaded_projects = pl.cv.size();
					}
				}
			} catch (Exception e) {
				Debug.Log(Debug.INFO,
						"ProjectManager work queue exception - exiting " + e);
				stop();
			}
		}
		Debug.Log(Debug.INFO, "ProjectManager stopped.");
	}

	void processList(ProjectList pl) {
		String pll[] = new String[pl.cv.size()];
		Debug.Log(Debug.TRACE, "size is " + pl.cv.size());
		for (int i = 0; i < pl.cv.size(); i++) {
			Project p = (Project) pl.cv.get(i);

			// Get the base name from the repo.
			p.BaseName = "";
			if (p.Repo.indexOf("https") == 0) {

				String[] tokens1 = p.Repo.split("/");
				Debug.Log(Debug.TRACE, "http parsed out base name "
						+ tokens1[tokens1.length - 1]);
				String parsed = tokens1[tokens1.length - 1];
				String[] tokens2 = parsed.split("\\.");
				if (tokens2.length == 2) {
					parsed = tokens2[0];
					Debug.Log(Debug.TRACE, "reparsed base name " + parsed);
				}

				p.BaseName = new String(Config.getProjectDir() + "/" + parsed);
				p.BaseNameMinimal = new String(parsed);

			} else {

				String[] tokens1 = p.Repo.split("/");
				if (tokens1 != null && tokens1.length == 2) {
					String[] tokens2 = tokens1[1].split("\\.");
					Debug.Log(Debug.TRACE, "git parsed out base name "
							+ tokens2[0]);
					p.BaseName = new String(Config.getProjectDir() + "/"
							+ tokens2[0]);
					p.BaseNameMinimal = new String(tokens2[0]);
				} else {
					p.BaseName = p.BaseNameMinimal = "";
				}
			}

			// Set generic builder type for now.
			p.BuilderType = Builder.BUILDER_TYPE_GENERIC;
			Debug.Log(Debug.TRACE, "adding " + p.Name + " " + p.Repo);
			pll[i] = "user " + p.UserId + " repo " + p.Repo;
		}
		Status.project_list = pll;
	}
}
