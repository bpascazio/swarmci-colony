package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Config;

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
				// pull code from git here then queue a build
			} catch (Exception e) {
				Debug.Log(Debug.TRACE, "BuildManager work queue exception - exiting");
				stop();
			}
		}
		Debug.Log(Debug.INFO, "BuildManager stopped.");
	}
}
