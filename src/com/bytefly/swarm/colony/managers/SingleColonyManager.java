package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.Status;

public class SingleColonyManager extends Manager {

	Manager cm;
	Manager pm;
	Manager gm;
	Manager bm;

	public SingleColonyManager(Manager _cm, Manager _pm, Manager _gm, Manager _bm) {
		super(true);
		cm = _cm;
		pm = _pm;
		gm = _gm;
		bm = _bm;
	}

	public void run() {
		Debug.Log(Debug.INFO, "SingleColonyManager started.");
		Status.counter_initial_uptime = System.currentTimeMillis();
		start();

		int cproject = 0;
		int cscan = 0;
		int ccloud = 0;
		Work w = null;

		while (running) {

			//
			// Swarm main processing is a thread that waks up 4 times a second
			// (for now).
			// It checks its counters to see if it needs to remind any of the
			// managers to do some work.
			// For example it tells the project manager to hit the rails server
			// for new projects every
			// 5 seconds. It also tells the git manager to check all the locked
			// and loaded repositories
			// for changes every 1 second. These values are all controllable by
			// config and properties.
			//

			if (ccloud == 0) {

				// send command to git manager
				w = new Work(Work.WORK_ITEM_CLOUD_CHECK_CONNECTION);
				try {
					Debug.Log(Debug.TRACE, "SingleColonyManager handing to cloud manager.");					
					cm.put(w);
				} catch (Exception e) {
					Debug.Log(Debug.INFO,
							"SingleColonyManager exception putting work item - stopping.");
					stop();
				}
				// reset the counter
				ccloud = Config.getIntValue(Config.SWARM_CLOUD_CHECK_FREQ)
						+ Config.getIntValue(Config.SWARM_CLOUD_CHECK_FREQ);
			} else {
				ccloud = ccloud
						- Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			}

			if (cproject == 0) {

				// Only issue project scan if cloud is connected
				if (((CloudManager)cm).connected()) {

					// send command to project manager
					w = new Work(Work.WORK_ITEM_PROJECT_FETCH_PROJECTS);
					try {
						Debug.Log(Debug.TRACE, "SingleColonyManager handing to project manager.");
						pm.put(w);
					} catch (Exception e) {
						Debug.Log(Debug.INFO,
								"SingleColonyManager exception putting work item - stopping.");
						stop();
					}

				} else {
					Debug.Log(Debug.INFO,
							"SingleColonyManager not pulling projects, cloud is not connected.");
					
				}

				// reset the counter
				cproject = Config.getIntValue(Config.SWARM_PROJECT_CHECK_FREQ)
						+ Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			} else {
				cproject = cproject
						- Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			}

			if (cscan == 0) {

				// Only issue git change scan if cloud is connected
				if (((CloudManager)cm).connected()) {

					// send command to git manager
					w = new Work(Work.WORK_ITEM_GIT_SCAN_PROJECTS);
					try {
						Debug.Log(Debug.TRACE, "SingleColonyManager handing to git manager.");
						gm.put(w);
					} catch (Exception e) {
						Debug.Log(Debug.INFO,
								"SingleColonyManager exception putting work item - stopping.");
						stop();
					}

				} else {
					Debug.Log(Debug.INFO,
							"SingleColonyManager not scanning projects, cloud is not connected.");
					
				}

				// reset the counter
				cscan = Config.getIntValue(Config.SWARM_GIT_CHECK_FREQ)
						+ Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			} else {
				cscan = cscan - Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			}

			// Check to see if we've gone to a connected state, this auto clears
			if (((CloudManager)cm).connectTriggerLatchTransition()) {
				
				// Reset everthing since now connected
				cproject = cscan = 0;
				
				Debug.Log(Debug.INFO,
						"SingleColonyManager resetting due to new connection state.");
			}
			
			try {

				Status.counter_heartbeat++;
				
				// Time to sleep for a bit.
				Thread.sleep(Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ));

			} catch (Exception e) {
				Debug.Log(Debug.INFO, "SingleColonyManager thread wake.");
			}

		}
		Debug.Log(Debug.INFO, "SingleColonyManager no longer running.");
	}
}
