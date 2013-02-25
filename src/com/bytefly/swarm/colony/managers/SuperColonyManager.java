package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class SuperColonyManager extends Manager {

	public SuperColonyManager() {
		super(false);
	}

	public void run() {
		Debug.Log(Debug.INFO, "SuperColonyManager started.");
		Status.counter_initial_uptime = System.currentTimeMillis();
		start();


		while (running) {
		
			try {

				Status.counter_heartbeat++;
				
				// Time to sleep for a bit.
				Thread.sleep(Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ));

			} catch (Exception e) {
				Debug.Log(Debug.INFO, "SuperColonyManager thread wake.");
			}

		}
		Debug.Log(Debug.INFO, "SuperColonyManager no longer running.");
	}
}

