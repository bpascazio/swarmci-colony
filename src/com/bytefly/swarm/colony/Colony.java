package com.bytefly.swarm.colony;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

import com.bytefly.swarm.colony.managers.*;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Version;

public class Colony extends Thread {

	public static void main(String[] args) {

		startServer();
	}

	public void run() {
		startServer();
	}

	public static void startServer() {
		Debug.Log("Swarm Colony Server Starting...");
		Debug.Log("Version is " + Version.getVersion() + " Build "
				+ Version.getBuildNum());

		if (getPreference() == true) {

			// build manager triggered on commit updates to build product
			BuildManager bm = new BuildManager();

			// git manager scans all projects for commit updates
			GitManager gm = new GitManager(bm);

			// project manager pulls all projects from api
			ProjectManager pm = new ProjectManager(gm);

			// swarm manager manages lifecycle of server
			SwarmManager sm = new SwarmManager(pm, gm, bm);
			sm.run();
		} else {
			Debug.Log("No colony.preferences file found - aborting start...");
		}
	}

	public static boolean getPreference() {
		// create and load default properties
		Properties colonyProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("colony.properties");
			colonyProps.load(in);
			in.close();
			Config.setRailsServer(colonyProps.getProperty(Config.SWARM_RAILS_URL));
			String ll = colonyProps.getProperty(Config.DEBUG_LOG_LEVEL);
			Debug.setLevel(ll);
			Debug.Log("Logging level set to :"+ll);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
