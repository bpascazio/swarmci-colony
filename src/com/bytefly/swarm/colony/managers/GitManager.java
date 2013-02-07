package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.collections.ProjectList;


public class GitManager extends Manager {
	
	BuildManager bm;
	
	public GitManager(BuildManager m) {
		bm = m;
	}
	
	public void run() {
		Debug.Log(Debug.INFO, "GitManager started.");
//		Debug.Log(Debug.INFO, "getting project list");
//		ProjectList pl = new ProjectList();
		while (running) {
			Debug.Log(Debug.TRACE, "GitManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE, "GitManager received item "+w.toString());
				// pull code from git here then queue a build
				bm.put(w);
			} catch (Exception e) {
				Debug.Log(Debug.TRACE, "GitManager work queue exception - exiting");
				stop();
			}
//			try {
//				Thread.sleep(Config.getIntValue(Config.SWARM_GIT_CHECK_FREQ));
//				pl.refresh();
//			} catch (Exception e) {
//			}
		}
		Debug.Log(Debug.INFO, "GitManager stopped.");
	}
}
