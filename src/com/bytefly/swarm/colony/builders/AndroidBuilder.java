package com.bytefly.swarm.colony.builders;

import java.io.File;

import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.util.HttpConnector;

public class AndroidBuilder extends Builder {

	String apkName;
	
	public void runAll() {
		File f = new File(p.BaseName);
		if(f.exists()) { 
			Debug.Log(Debug.TRACE, "Git repo exists");
			this.repoUpdate();
		} else {
			this.repoClone();
		}
		androidRemoveBinDirs();
		andoidBuild();
		apkName="";
		androidGetAPKName();
		Build bd = new Build();
		bd.user_id = p.UserId;
		bd.project_id = p.ProjectId;
		bd.success = false;
		if (apkName.equals("")) {
			Debug.Log(Debug.TRACE, "no apk found");			
		} else {
			Debug.Log(Debug.TRACE, "apk found uploading and emailing");						
			androidUploadBuild();
			androidSendEmail();
			bd.success = true;
		}
		HttpConnector h = new HttpConnector();
		h.setEntity(bd);
	}
	
	public void androidRemoveBinDirs() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_CLEARN_CMD));
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_CLEARN_CMD),null,new File(this.p.BaseName));
			pr.waitFor(); 
			Debug.Log(Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running androidRemoveBinDirs "+e.toString());
		}		
	}
	
	public void andoidBuild() {
		try {
			String target="release";
			if (p.debug)target="debug";
			Debug.Log(Debug.DEBUG, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_BUILD_CMD)+" "+target+" in directory "+this.p.BaseName+"/"+this.p.buildDirectory);
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_BUILD_CMD)+" "+target,null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			Debug.Log(Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running androidRemoveBinDirs "+e.toString());
		}
	}
	
	public void androidGetAPKName() {
		
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_APP_NAME));
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_APP_NAME),null,new File(this.p.BaseName));
			pr.waitFor(); 
			apkName = new String(getOutAndErrStream(pr));
			apkName = apkName.replace("\n", "");
			Debug.Log(Debug.TRACE, "apkName="+apkName);
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}
	}

	public void androidUploadBuild() {

		try {
			String cmd = 
					String.format(Config.getStringValue(Config.SWARM_ANDROID_UPLOAD_APK), 
							this.apkName, this.p.BaseName+".apk");
			Debug.Log(Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File(this.p.BaseName));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}
	}
	
	public void androidSendEmail() {

		try {
			String name = p.BaseName+"-"+p.Version+"-";
			String owner = "bytefly";
			String repo = p.BaseName;
			String to = "qa@bytefly.com";
			String cmd = 
					String.format(Config.getStringValue(Config.SWARM_ANDROID_SEND_EMAIL_APK), 
							name, p.buildNum, this.p.BaseName+".apk", owner, repo, to);
			Debug.Log(Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File(this.p.BaseName));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}	
	}
}
