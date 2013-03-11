package com.bytefly.swarm.colony.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.omg.CORBA.portable.InputStream;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.models.Logfile;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;

public class XCodeBuilder extends Builder {

	public void runAll() {
		File f = new File(p.BaseName);
		if(f.exists()) { 
			Debug.Log(Debug.TRACE, "XCodeBuilder Git repo exists");
			this.repoUpdate();
		} else {
			this.repoClone();
		}
//		androidRemoveBinDirs();
		p.logFile = new Logfile();
		p.logFile.startLogFile(p.BaseNameMinimal+p.buildNum+".log", p.BaseName);
		xcodeBuild();
//		androidSetSDK();
//		androidVerifyCreateBuildXml();
//		androidRunScripts();
//		andoidBuild();
//		apkName="";
//		androidGetAPKName();
		Build bd = new Build();
		bd.user_id = p.UserId;
		bd.project_id = p.ProjectId;
		bd.success = false;
//		if (apkName.equals("")) {
			Debug.Log(Debug.TRACE, "XCodeBuilder no apk found");			
			Status.counter_builds_failure++;
			p.reason="Failed%20during%20compile.";
			p.logFile.stopLogFile();
			sendFailureEmail();
//		} else {
//			Debug.Log(Debug.TRACE, "apk found uploading and emailing");						
//			Status.counter_builds_success++;
//			androidUploadBuild();
//			p.logFile.stopLogFile();
//			androidSendEmail();
//			p.reason="Built.";
//			bd.success = true;
//		}
		bd.info=p.reason;
		if (bd.info.equals("Failed%20during%20compile."))bd.info="Failed during compile.";
		if (p.logFile!=null) {
			bd.logs = Config.getStringValue(Config.SWARM_LOG_PREFIX)+this.p.BaseNameMinimal+this.p.buildNum+".log";
		}
		bd.project_name=p.Name;
		bd.bldnum = p.buildNum;
		bd.bldtype = Build.BUILD_TYPE_IOS;
		HttpConnector h = new HttpConnector();
		h.setEntity(bd);

	}
	
	public void xcodeBuild() {
		try {
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_PWD),null,new File("."));
			pr.waitFor(); 
			String xcodeBuildPath = new String(getOutAndErrStream(pr));
			xcodeBuildPath = xcodeBuildPath.replace("\n", "");
			Debug.Log(Debug.TRACE, "xcodeBuild dir "+xcodeBuildPath+"/"+this.p.buildDirectory); 
			
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_XCODE_BUILD_CMD));
			pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_XCODE_BUILD_CMD),null,new File(xcodeBuildPath+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			String result = getOutAndErrStream(pr);
			Debug.Log(Debug.TRACE, "xcodeBuild="+result);
			p.logFile.writeToLog(result);
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running xcodeBuild "+e.toString());
		}
	}

}
