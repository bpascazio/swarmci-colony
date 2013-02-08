package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class SwarmManager extends Manager {
	
	Manager pm;
	Manager gm;
	Manager bm;
	
	public SwarmManager(Manager _pm, Manager _gm, Manager _bm) {
		super(false);
		pm = _pm;
		gm = _gm;
		bm = _bm;
	}
	
	public void run() {
		Debug.Log(Debug.INFO, "SwarmManager started.");
		start();
		
		int cproject = 0;
		int cscan = 0;
		Work w = null;
		
		
		while (running) {

			//
			// Swarm main processing is a thread that waks up 4 times a second (for now).
			// It checks its counters to see if it needs to remind any of the managers to do some work.
			// For example it tells the project manager to hit the rails server for new projects every
			// 5 seconds.  It also tells the git manager to check all the locked and loaded repositories
			// for changes every 1 second.  These values are all controllable by config and properties.
			//
			
			if (cproject == 0) {
				
				// send command to project manager
				w = new Work(Work.WORK_ITEM_PROJECT_FETCH_PROJECTS);
				try {
					pm.put(w);
				} catch (Exception e) {
					Debug.Log(Debug.INFO,
							"SwarmManager exception putting work item - stopping.");
					stop();
				}
				
				// reset the counter
				cproject = Config.getIntValue(Config.SWARM_PROJECT_CHECK_FREQ)+Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			} else {
				cproject = cproject - Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			}
			
			if (cscan == 0) {

				// send command to git manager
				w = new Work(Work.WORK_ITEM_GIT_SCAN_PROJECTS);
				try {
					gm.put(w);
				} catch (Exception e) {
					Debug.Log(Debug.INFO,
							"SwarmManager exception putting work item - stopping.");
					stop();
				}				
				// reset the counter
				cscan = Config.getIntValue(Config.SWARM_GIT_CHECK_FREQ)+Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			} else {
				cscan = cscan - Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ);
			}
			
			try {
				
				// Time to sleep for a bit.
				Thread.sleep(Config.getIntValue(Config.SWARM_MGR_CHECK_FREQ));
				
			} catch (Exception e) {
				Debug.Log(Debug.INFO, "SwarmManager thread wake.");
			}

		}
	}
}
