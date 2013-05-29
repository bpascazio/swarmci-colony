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

	private static SecurityContext mSecurityContext = null;
	
	public static void main(String[] args) {

		startServer();
	}

	public void run() {
		startServer();
	}

	public static void setSecurityContext(SecurityContext c) {
		mSecurityContext = c;
	}
	
	public static void startServer() {

		if (getPreference() == true) {
			
			// Setup security context.
			SecurityContext sc = new SecurityContext();
			setSecurityContext(sc);

			if (Config.getSuperColonyMode().equals("")) {
			
				// Header info
				Debug.Log("Swarm Colony Server Starting...");
				Debug.Log("Version is " + Version.getVersion() + " Build "
					+ Version.getBuildNum());

				// cloud manager handles connection to cloud
				CloudManager cm = new CloudManager(mSecurityContext);

				// build manager triggered on commit updates to build product
				BuildManager bm = new BuildManager(mSecurityContext);

				// git manager scans all projects for commit updates
				GitManager gm = new GitManager(mSecurityContext, bm);

				// project manager pulls all projects from api
				ProjectManager pm = new ProjectManager(gm);

				// swarm single manager manages lifecycle of a single colony server
				SingleColonyManager sm = new SingleColonyManager(cm, pm, gm, bm);
				
				// status monitor
				StatusManager stm = new StatusManager(sm);
				stm.run();
			
			} else {
				
				// Header info
				Debug.Log("Swarm SUPER COLONY Starting...");
				Debug.Log("Version is " + Version.getVersion() + " Build "
					+ Version.getBuildNum());

				// swarm single manager manages lifecycle of a single colony server
				SuperColonyManager scm = new SuperColonyManager();
				scm.run();
				
			}
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
			String ac = colonyProps.getProperty(Config.SWARM_PROJECT_ALWAYS_CLEAN);
			if (ac!=null)Config.setAlwaysClean(ac);
			String pra = colonyProps.getProperty(Config.SWARM_PROJECT_POLL_RATE);
			if (pra!=null)Config.setPollRate(pra);
			String lt = colonyProps.getProperty(Config.SWARM_PROJECT_LIFETIME);
			if (lt!=null)Config.setLifetime(lt);
			Debug.Log("Directory set to :"+dir);
			String asdk = colonyProps.getProperty(Config.SWARM_ANDROID_SDK);
			Config.setAndroidSDK(asdk);
			Debug.Log("Android SDK set to :"+asdk);
			String cfg = colonyProps.getProperty(Config.SWARM_COLONY_CONFIG);
			if (cfg==null)cfg="";
			Config.setConfigPath(cfg);			
			Debug.Log("Swarm config set to :"+cfg);

			String scm = colonyProps.getProperty(Config.SWARM_SUPERCOLONY_MODE);
			if (scm==null)scm="";
			Config.setSuperColonyMode(scm);	
			if (scm.equals("")) {
				Debug.Log("Swarm Standard Colony Server Created");
			} else {
				Debug.Log("Swarm SUPER COLONY Created servers="+scm);
			}

			
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
