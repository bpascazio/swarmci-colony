package com.bytefly.swarm.colony.managers;
/*
 * Periodically gets project collection.
 */
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.collections.ProjectList;

public class ProjectManager extends Manager {
	
	GitManager gm;
	
	public ProjectManager(GitManager m) {
		gm = m;
	}
	public void run() {
		Debug.Log(Debug.INFO, "ProjectManager started.");
		while (running) {
			Debug.Log(Debug.INFO, "ProjectManager getting project list.");
			ProjectList pl = new ProjectList();
			Work w = new Work(Work.UPDATE_PROJECTS_WORK_ITEM);
			w.data = pl;
			try {
				gm.put(w);
			} catch (Exception e) {
				Debug.Log(Debug.INFO, "ProjectManager exception putting work item - stopping.");
				stop();
			}
			try {
				Thread.sleep(Config.getIntValue(Config.SWARM_PROJECT_CHECK_FREQ));
				pl.refresh();
			} catch (Exception e) {
			}
		}
		Debug.Log(Debug.INFO, "ProjectManager stopped.");
	}
}
