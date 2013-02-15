package com.bytefly.swarm.colony;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

import com.bytefly.swarm.colony.managers.*;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;
import com.bytefly.swarm.colony.util.Version;

public class Colony extends Thread {

	public static void main(String[] args) {

		startServer();
	}

	public void run() {
		startServer();
	}

	public static void startServer() {

		if (getPreference() == true) {
			
			// Header info
			Debug.Log("Swarm Colony Server Starting...");
			Debug.Log("Version is " + Version.getVersion() + " Build "
					+ Version.getBuildNum());

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
			String lfile = colonyProps.getProperty(Config.SWARM_DEBUG_LOG_FILE);
			Config.setLogFile(lfile);
			Debug.Log("Log file output set to :"+lfile);
			Config.setRailsServer(colonyProps.getProperty(Config.SWARM_RAILS_URL));
			String ll = colonyProps.getProperty(Config.SWARM_DEBUG_LOG_LEVEL);
			Debug.setLevel(ll);
			Debug.Log("Logging level set to :"+ll);
			String uid = colonyProps.getProperty(Config.SWARM_COLONY_UUID);
			Config.setColonyUUID(uid);
			Debug.Log("UUID set to :"+uid);
			String dir = colonyProps.getProperty(Config.SWARM_PROJECT_DIR);
			Config.setProjectDir(dir);
			Debug.Log("Directory set to :"+dir);
			String asdk = colonyProps.getProperty(Config.SWARM_ANDROID_SDK);
			Config.setAndroidSDK(asdk);
			Debug.Log("Android SDK set to :"+asdk);
			
			Debug.Log(Debug.TRACE, "Creating directory "+dir);
			try {
				Process pr = Runtime.getRuntime().exec(
						Config.getStringValue(Config.SWARM_MAKE_PROJECT_DIR) + " "
								+ dir);
				pr.waitFor();
			} catch (Exception e) {
				Debug.Log(Debug.INFO,
						"Exception caught creating project dir " + e.toString());
			}

			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
