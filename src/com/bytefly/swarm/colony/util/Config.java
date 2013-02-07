package com.bytefly.swarm.colony.util;

import com.bytefly.swarm.colony.util.Debug;

public class Config {
	public static final String SWARM_PROJECT_CHECK_FREQ = "SWARM_PROJECT_CHECK_FREQ";
	public static final String SWARM_GIT_CHECK_FREQ = "SWARM_GIT_CHECK_FREQ";
	public static final String SWARM_MGR_CHECK_FREQ = "SWARM_MGR_CHECK_FREQ";
	public static final String SWARM_RAILS_URL = "SWARM_RAILS_URL";
	public static final String SWARM_DEFAULT_QUEUE_SIZE = "SWARM_DEFAULT_QUEUE_SIZE";

	public static int getIntValue(String key) {
		if (key.equals(SWARM_PROJECT_CHECK_FREQ)) {
			return 5000; // fetch project from rails server every 5 seconds
		}
		if (key.equals(SWARM_GIT_CHECK_FREQ)) {
			return 1000; // scan for updated git repositories every second
		}
		if (key.equals(SWARM_MGR_CHECK_FREQ)) {
			return 250; // swarm manager checks for commands every 4 times a second
		}
		if (key.equals(SWARM_DEFAULT_QUEUE_SIZE)) {
			return 25; // default maximum work items in queue
		}
		
		Debug.Log(Debug.DEBUG, "Undefined Config Value " + key);
		return 0;
	}

	public static String getStringValue(String key) {
		if (key.equals(SWARM_RAILS_URL)) {
			return "http://localhost:3000";
		}
		Debug.Log(Debug.DEBUG, "Undefined Config Value " + key);
		return "";
	}
}
