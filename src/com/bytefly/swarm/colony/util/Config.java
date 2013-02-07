package com.bytefly.swarm.colony.util;

import com.bytefly.swarm.colony.util.Debug;

public class Config {
	public static final String SWARM_PROJECT_CHECK_FREQ = "SWARM_PROJECT_CHECK_FREQ";
	public static final String SWARM_BUILD_CHECK_FREQ = "SWARM_BUILD_CHECK_FREQ";
	public static final String SWARM_MGR_CHECK_FREQ = "SWARM_MGR_CHECK_FREQ";
	public static final String SWARM_RAILS_URL = "SWARM_RAILS_URL";
	public static final String SWARM_DEFAULT_QUEUE_SIZE = "SWARM_DEFAULT_QUEUE_SIZE";

	public static int getIntValue(String key) {
		if (key.equals(SWARM_PROJECT_CHECK_FREQ)) {
			return 5000; // ms
		}
		if (key.equals(SWARM_BUILD_CHECK_FREQ)) {
			return 1000; // ms
		}
		if (key.equals(SWARM_MGR_CHECK_FREQ)) {
			return 250; // ms
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
