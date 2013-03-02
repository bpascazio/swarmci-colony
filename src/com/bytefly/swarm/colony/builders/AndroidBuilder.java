package com.bytefly.swarm.colony.builders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.models.Logfile;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;

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
		p.logFile = new Logfile();
		p.logFile.startLogFile(p.BaseNameMinimal+p.buildNum+".log", p.BaseName);
		androidSetSDK();
		androidVerifyCreateBuildXml();
		andoidBuild();
		apkName="";
		androidGetAPKName();
		Build bd = new Build();
		bd.user_id = p.UserId;
		bd.project_id = p.ProjectId;
		bd.success = false;
		if (apkName.equals("")) {
			Debug.Log(Debug.TRACE, "no apk found");			
			Status.counter_builds_failure++;
			p.reason="Failed%20during%20compile.";
			p.logFile.stopLogFile();
			sendFailureEmail();
		} else {
			Debug.Log(Debug.TRACE, "apk found uploading and emailing");						
			Status.counter_builds_success++;
			androidUploadBuild();
			p.logFile.stopLogFile();
			androidSendEmail();
			bd.success = true;
		}
		HttpConnector h = new HttpConnector();
		h.setEntity(bd);
	}
	
	public void androidRemoveBinDirs() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_CLEARN_CMD)+" in directory "+this.p.BaseName+"/"+this.p.buildDirectory);
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_CLEARN_CMD),null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			Debug.Log(Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running androidRemoveBinDirs "+e.toString());
		}		
	}
	
	public void androidSetSDK() {
		try {
			String lfile="sdk.dir="+Config.getAndroidSDK()+"\n";
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(this.p.BaseName+"/"+this.p.buildDirectory+"local.properties", true));
			bw.write(lfile);
			bw.flush();
			Debug.Log(Debug.TRACE, "android sdk set to "+Config.getAndroidSDK());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Debug.Log(Debug.INFO, "Exception caught running androidSetSDK "+e.toString());
		}

	}
	
	public void androidVerifyCreateBuildXml() {
		Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_FIND_BUILDXML)+"");
		Process pr;
		try {
			pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_FIND_BUILDXML),null,new File(this.p.BaseName));
			pr.waitFor(); 
			String androidxPath = new String(getOutAndErrStream(pr));
			androidxPath = androidxPath.replace("\n", "");
			Debug.Log(Debug.TRACE, "androidxPath "+androidxPath);
			if (androidxPath.equals("")) {
				String createbxml = Config.getAndroidSDK()+"/"+Config.getStringValue(Config.SWARM_ANDROID_GENERATE_BUILDXML);
				Debug.Log(Debug.TRACE, "Executing "+createbxml);
				pr = Runtime.getRuntime().exec(createbxml,null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
				pr.waitFor(); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.Log(Debug.INFO, "Exception caught running androidVerifyCreateBuildXml "+e.toString());
		}
	}
	
	public void andoidBuild() {
		try {
			String target="release";
			if (p.debug)target="debug";
			Debug.Log(Debug.DEBUG, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_BUILD_CMD)+" "+target+" in directory "+this.p.BaseName+"/"+this.p.buildDirectory);
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_BUILD_CMD)+" "+target,null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			String result = getOutAndErrStream(pr);
			p.logFile.writeToLog(result);
			Debug.Log(Debug.TRACE, "result="+result);
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
							this.apkName, this.p.BaseNameMinimal+this.p.buildNum+".apk");
			Debug.Log(Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File(this.p.BaseName));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}
	}
	
	public void androidSendEmail() {

		try {
			String name = p.BaseNameMinimal+"-"+p.Version+"."+p.buildNum;
			String owner = p.Owner;
			String repo = p.BaseName;
			String to = this.toList;
			String log = this.p.BaseNameMinimal+this.p.buildNum+".log";
			String cmd = 
					String.format(Config.getStringValue(Config.SWARM_ANDROID_SEND_EMAIL_APK), 
							name, p.buildNum, this.p.BaseNameMinimal+this.p.buildNum+".apk", log, owner, repo, to, p.commit);
			Debug.Log(Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File(this.p.BaseName));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}	
	}
}
