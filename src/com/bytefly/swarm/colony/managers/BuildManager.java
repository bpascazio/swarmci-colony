package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Config;

public class BuildManager extends Manager {

	public void run() {
		this.start();
		Debug.Log(Debug.INFO, "Build manager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "processing build queue");
			try {
				Thread.sleep(Config.getIntValue(Config.SWARM_BUILD_CHECK_FREQ));
			} catch (Exception e) {

			}
		}
	}
}
