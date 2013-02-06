package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.collections.ProjectList;

public class GitManager extends Manager {
	public void run() {
		Debug.Log(Debug.INFO, "Git manager started.");
		Debug.Log(Debug.INFO, "getting project list");
		ProjectList pl = new ProjectList();
		while (running) {
			Debug.Log(Debug.TRACE, "processing git queue");
			try {
				Thread.sleep(Config.getIntValue(Config.SWARM_GIT_CHECK_FREQ));
				pl.refresh();
			} catch (Exception e) {

			}
		}
	}
}
