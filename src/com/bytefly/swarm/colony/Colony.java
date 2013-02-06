package com.bytefly.swarm.colony;

import com.bytefly.swarm.colony.managers.*;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Version;

public class Colony {
	public static void main(String[] args) {
		Debug.Log(Debug.INFO, "Swarm Colony Server Starting...");
		Debug.Log(Debug.INFO, "Version is "+Version.getVersion()+" Build "+Version.getBuildNum());
		GitManager gm = new GitManager();
		BuildManager bm = new BuildManager();
		SwarmManager sm = new SwarmManager();
		sm.run();
	}
}
