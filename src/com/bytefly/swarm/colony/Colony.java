package com.bytefly.swarm.colony;

import com.bytefly.swarm.colony.managers.*;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Version;

public class Colony {
	public static void main(String[] args) {
		Debug.Log(Debug.INFO, "Swarm Colony Server Starting...");
		Debug.Log(Debug.INFO, "Version is "+Version.getVersion()+" Build "+Version.getBuildNum());
		
		// build manager triggered on commit updates to build product
		BuildManager bm = new BuildManager();
		
		// git manager scans all projects for commit updates
		GitManager gm = new GitManager(bm);
		
		// project manager pulls all projects from api
		ProjectManager pm = new ProjectManager(gm);
		
		// swarm manager manages lifecycle of server
		SwarmManager sm = new SwarmManager();
		sm.run();
	}
}
