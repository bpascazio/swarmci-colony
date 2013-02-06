package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class GitManager extends Manager {
	public void run() {
		Debug.Log(Debug.INFO, "Git manager started.");
		while (running) {
			Debug.Log(Debug.TRACE, "processing git queue");
			try {
				Thread.sleep(Config.getIntValue(Config.SWARM_GIT_CHECK_FREQ));
			} catch (Exception e) {

			}
		}
	}
}
