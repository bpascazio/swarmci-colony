package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Debug;

// Work is to build an app and send out.

public class BuildManager extends Manager {

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
					Debug.Log(Debug.DEBUG, "BuildManager executing build");
				}				
			} catch (Exception e) {
				Debug.Log(Debug.INFO, "BuildManager work queue exception - exiting");
				stop();
			}
		}
		Debug.Log(Debug.INFO, "BuildManager stopped.");
	}
}
