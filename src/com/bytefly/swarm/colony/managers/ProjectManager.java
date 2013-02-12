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
					Work nw = new Work(Work.WORK_ITEM_UPDATE_PROJECTS);
					nw.data = pl;
					try {
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
							gm.put(gw);
						} catch (Exception e) {
							Debug.Log(Debug.INFO,
									"ProjectManager exception putting work item - stopping.");
							stop();
						}
					}
					Status.counter_loaded_projects = pl.cv.size();
				}
			} catch (Exception e) {
				Debug.Log(Debug.INFO,
						"ProjectManager work queue exception - exiting");
				stop();
			}
		}
		Debug.Log(Debug.INFO, "ProjectManager stopped.");
	}

	void processList(ProjectList pl) {
		for (int i = 0; i < pl.cv.size(); i++) {
			Project p = (Project) pl.cv.get(i);

			// Get the base name from the repo.
			p.BaseName = "";
			if (p.Repo.indexOf("https") == 0) {

				String[] tokens1 = p.Repo.split("/");
				Debug.Log(Debug.TRACE, "http parsed out base name "
						+ tokens1[tokens1.length - 1]);
				p.BaseName = Config.getProjectDir()+"/"+tokens1[tokens1.length - 1];

			} else {

				String[] tokens1 = p.Repo.split("/");
				String[] tokens2 = tokens1[1].split("\\.");
				Debug.Log(Debug.TRACE, "git parsed out base name " + tokens2[0]);
				p.BaseName = Config.getProjectDir()+"/"+tokens2[0];
			}

			// Set generic builder type for now.
			p.BuilderType = Builder.BUILDER_TYPE_GENERIC;
			Debug.Log(Debug.TRACE, "adding " + p.Name + " " + p.Repo);
		}
	}
}
