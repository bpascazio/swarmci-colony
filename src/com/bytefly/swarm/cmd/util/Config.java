package com.bytefly.swarm.cmd.util;

import com.bytefly.swarm.colony.util.Debug;

public class Config {

	public static final String SWARM_RAILS_URL = "SWARM_RAILS_URL";

	public static int getIntValue(String key) {
		Debug.Log(Debug.DEBUG, "Undefined Config Value " + key);
		return 0;
	}

	public static String getStringValue(String key) {
		if (key.equals(SWARM_RAILS_URL)) {
			return "http://swarm-cloud.herokuapp.com/";
		}
		Debug.Log(Debug.DEBUG, "Undefined Config Value " + key);
		return "";
	}
}
