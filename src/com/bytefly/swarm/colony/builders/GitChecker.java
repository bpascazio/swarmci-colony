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
				Debug.Log(Debug.TRACE, "buildTypeChecker android manifest not found looking for xcode");
				
				Debug.Log(Debug.TRACE, "buildTypeChecker Executing "+Config.getStringValue(Config.SWARM_XCODE_FIND_XCODEPROJ)+"");
				pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_XCODE_FIND_XCODEPROJ),null,new File(this.p.BaseName));
				pr.waitFor(); 
				String xcodeProjectPath = new String(getOutAndErrStream(pr));
				xcodeProjectPath = xcodeProjectPath.replace("\n", "");

				Debug.Log(Debug.TRACE, "buildTypeChecker result is "+xcodeProjectPath);

				if (!xcodeProjectPath.equals("")) {

					// Get the android build directory from the repo.
					String[] tokens1 = xcodeProjectPath.split("/");
					String[] tokens2 = xcodeProjectPath.split(tokens1[tokens1.length-1]);
					Debug.Log(Debug.TRACE, "buildTypeChecker parsed out build path " + tokens2[0]);
					p.buildDirectory =  this.p.BaseName+"/"+tokens2[0];
					Debug.Log(Debug.TRACE, "buildTypeChecker buildDirectory="+p.buildDirectory);
					p.BuilderType =  Builder.BUILDER_TYPE_XCODE;
				}
				
			} else {
				Debug.Log(Debug.TRACE, "buildTypeChecker androidManifestPath="+androidManifestPath);
				
				// Get the android build directory from the repo.
				String[] tokens1 = androidManifestPath.split("AndroidManifest\\.xml");
				Debug.Log(Debug.TRACE, "buildTypeChecker parsed out build path " + tokens1[0]);
				p.buildDirectory =  tokens1[0];
				
				// Make sure build.xml is there.
				Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_FIND_MANIFEST)+"");
				Process pr2 = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_FIND_MANIFEST),null,new File(this.p.BaseName));
				pr2.waitFor(); 
				String buildXMLPath = new String(getOutAndErrStream(pr2));
				buildXMLPath = buildXMLPath.replace("\n", "");
				
				if (buildXMLPath.equals("")) {
					
					// no build file found
					Debug.Log(Debug.DEBUG, p.Name+"no AndroidManifest.xml file found not valid android build buildTypeChecker");
				} else {
					p.BuilderType =  Builder.BUILDER_TYPE_ANDROID;
				}
			}
			if (p.BuilderType == Builder.BUILDER_TYPE_GENERIC) {
				Debug.Log(Debug.DEBUG, "flagging project "+p.Name+" as invalid in cloud due to no build files. buildTypeChecker");
			} else {
				Debug.Log(Debug.TRACE, p.Name+" ready to build if necessary. buildTypeChecker");
			}
			
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running buildTypeChecker "+e.toString());
			invalidGit = true;
		}
	}

}
