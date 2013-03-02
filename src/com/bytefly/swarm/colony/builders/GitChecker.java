package com.bytefly.swarm.colony.builders;

import java.io.File;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class GitChecker extends Builder {

	public String lastCheckin = null;
	public boolean invalidGit = false;
	
	public void runAll() {
		invalidGit = false;
		File f = new File(p.BaseName);
		if(f.exists()) { 
			Debug.Log(Debug.TRACE, "Git repo exists");
			this.repoUpdate();
		} else {
			if (p.forceClean) {
				this.repoClean();
				p.forceClean=false;
			}
			this.repoClone();
		}
		gitChecker();
		buildTypeChecker();
	}
	
	public void gitChecker() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_GIT_CHECK_CMD)+"");
			Status.counter_git_checks++;
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_GIT_CHECK_CMD),null,new File(this.p.BaseName));
			pr.waitFor(); 
			lastCheckin = new String(getOutAndErrStream(pr));
			lastCheckin = lastCheckin.replace("\n", "");
			Debug.Log(Debug.TRACE, "lastCheckin="+lastCheckin);
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running gitChecker "+e.toString());
			invalidGit = true;
		}
	}
	
	public void buildTypeChecker() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_FIND_MANIFEST)+"");
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_FIND_MANIFEST),null,new File(this.p.BaseName));
			pr.waitFor(); 
			String androidManifestPath = new String(getOutAndErrStream(pr));
			androidManifestPath = androidManifestPath.replace("\n", "");
			if (androidManifestPath.equals("")) {
				
				// No android path found, look for xcode
				Debug.Log(Debug.TRACE, "android manifest not found looking for xcode");
			} else {
				Debug.Log(Debug.TRACE, "androidManifestPath="+androidManifestPath);
				
				// Get the android build directory from the repo.
				String[] tokens1 = androidManifestPath.split("AndroidManifest\\.xml");
				Debug.Log(Debug.TRACE, "parsed out build path " + tokens1[0]);
				p.buildDirectory =  tokens1[0];
				
				// Make sure build.xml is there.
				Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_FIND_MANIFEST)+"");
				Process pr2 = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_FIND_MANIFEST),null,new File(this.p.BaseName));
				pr2.waitFor(); 
				String buildXMLPath = new String(getOutAndErrStream(pr2));
				buildXMLPath = buildXMLPath.replace("\n", "");
				
				if (buildXMLPath.equals("")) {
					
					// no build file found
					Debug.Log(Debug.DEBUG, p.Name+"no AndroidManifest.xml file found not valid android build");
				} else {
					p.BuilderType =  Builder.BUILDER_TYPE_ANDROID;
				}
			}
			if (p.BuilderType == Builder.BUILDER_TYPE_GENERIC) {
				Debug.Log(Debug.DEBUG, "flagging project "+p.Name+" as invalid in cloud due to no build files.");
			} else {
				Debug.Log(Debug.TRACE, p.Name+" ready to build if necessary.");
			}
			
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running buildTypeChecker "+e.toString());
			invalidGit = true;
		}
	}

}
