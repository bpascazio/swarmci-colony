package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Debug;

// Work is to receive lists of projects, check for Git Updates and queue Builds.

public class GitManager extends Manager {
	
	BuildManager bm;
	
	public GitManager(BuildManager m) {
		bm = m;
	}
	
	public void run() {
		Debug.Log(Debug.INFO, "GitManager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "GitManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE, "GitManager received item "+w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					//told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_GIT_SCAN_PROJECTS)) {
					Debug.Log(Debug.DEBUG, "GitManager scanning projects on github for changes...");
					// scan all the projects github repos and possibly queue builds
					
					w.name=Work.WORK_ITEM_BUILD_BUILD_PROJECT;
					bm.put(w); // fake queue build
				}			
			} catch (Exception e) {
				Debug.Log(Debug.INFO, "GitManager work queue exception - exiting");
				stop();
			}
		}
		Debug.Log(Debug.INFO, "GitManager stopped.");
	}
}
