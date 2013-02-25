package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class StatusManager extends Manager {

	Manager scm;

	public StatusManager(Manager _scm) {
		super(false);
		scm = _scm;
	}

	public void run() {
		Debug.Log(Debug.INFO, "StatusManager started.");
		Status.counter_initial_uptime = System.currentTimeMillis();
		start();

		int cstatus = Config.getIntValue(Config.SWARM_STATUS_CHECK_FREQ);
		int lastheartbeat = -1;
		
		while (running) {

			if (cstatus == 0) {

				if (Status.counter_heartbeat==lastheartbeat) {
					Status.colony_alive=false;
					Debug.Log(Debug.ERROR, "SingleColonyManager died.");					
				}
				// reset the counter
				cstatus = Config.getIntValue(Config.SWARM_STATUS_CHECK_FREQ)
						+ Config.getIntValue(Config.SWARM_STATUS_CHECK_FREQ);
			} else {
				cstatus = cstatus
						- Config.getIntValue(Config.SWARM_STATUS_CHECK_FREQ);
			}
			
			try {
				
				// Time to sleep for a bit.
				Thread.sleep(Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ));

			} catch (Exception e) {
				Debug.Log(Debug.INFO, "SingleColonyManager thread wake.");
			}

		}
		Debug.Log(Debug.INFO, "SingleColonyManager no longer running.");
	}
}

