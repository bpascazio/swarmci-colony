package com.bytefly.swarm.colony.managers;

// Work is to receive fetch collection projects and send to GitManager
 
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Debug;
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
				Debug.Log(Debug.TRACE, "ProjectManager received item "+w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					//told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_PROJECT_FETCH_PROJECTS)) {
					Debug.Log(Debug.DEBUG, "ProjectManager getting project list.");
					pl = new ProjectList();
					Work nw = new Work(Work.WORK_ITEM_UPDATE_PROJECTS);
					nw.data = pl;
					try {
						gm.put(nw);
					} catch (Exception e) {
						Debug.Log(Debug.INFO,
								"ProjectManager exception putting work item - stopping.");
						stop();
					}
				}			
			} catch (Exception e) {
				Debug.Log(Debug.INFO, "ProjectManager work queue exception - exiting");
				stop();
			}
		}
		Debug.Log(Debug.INFO, "ProjectManager stopped.");
	}
}
