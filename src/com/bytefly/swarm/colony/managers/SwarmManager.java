package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class SwarmManager extends Manager {
	public SwarmManager() {
		super(false);
	}
	
	public void run() {
		Debug.Log(Debug.INFO, "Swarm manager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "processing swarm");
			try {
				Thread.sleep(Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ));
			} catch (Exception e) {
				Debug.Log(Debug.DEBUG, "exception on sleep in manager");
			}
		}
	}
}
