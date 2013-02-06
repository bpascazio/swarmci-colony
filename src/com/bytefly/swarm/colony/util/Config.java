package com.bytefly.swarm.colony.util;

import com.bytefly.swarm.colony.util.Debug;

public class Config {
	public static final String SWARM_GIT_CHECK_FREQ = "SWARM_GIT_CHECK_FREQ";
	public static final String SWARM_BUILD_CHECK_FREQ = "SWARM_BUILD_CHECK_FREQ";
	public static final String SWARM_MGR_CHECK_FREQ = "SWARM_MGR_CHECK_FREQ";
	public static final String SWARM_RAILS_URL = "SWARM_RAILS_URL";

	public static int getIntValue(String key) {
		if (key.equals(SWARM_GIT_CHECK_FREQ)) {
			return 1000; // ms
		}
		if (key.equals(SWARM_BUILD_CHECK_FREQ)) {
			return 1000; // ms
		}
		if (key.equals(SWARM_MGR_CHECK_FREQ)) {
			return 250; // ms
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
