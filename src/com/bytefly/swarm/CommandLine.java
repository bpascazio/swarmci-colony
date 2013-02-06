package com.bytefly.swarm;

import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Version;

public class CommandLine {
	public static void main(String[] args) {
		if (args.length==0) {
			usage();
		}
	}
	public static void usage() {
		Debug.Log(Debug.INFO, "swarm ");
		Debug.Log(Debug.INFO, "Version is "+Version.getVersion()+" Build "+Version.getBuildNum());		
	}
}
