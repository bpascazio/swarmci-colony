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
	
	String appName;
	
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
//		andoidBuild();
		appName="";
		xcodeGetAppName();
		Build bd = new Build();
		bd.user_id = p.UserId;
		bd.project_id = p.ProjectId;
		bd.success = false;
		if (appName.equals("")) {
			Debug.Log(Debug.TRACE, "XCodeBuilder no app found");			
			Status.counter_builds_failure++;
			p.reason="Failed%20during%20compile.";
			p.logFile.stopLogFile();
			sendFailureEmail();
		} else {
			Debug.Log(Debug.TRACE, "app found uploading to testflight");						
			Status.counter_builds_success++;
			p.logFile.stopLogFile();
			xcodeRunScripts();
			p.reason="Built.";
			bd.success = true;
		}
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
	public void xcodeGetAppName() {
		
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_XCODE_APP_NAME));
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_XCODE_APP_NAME),null,new File(this.p.BaseName));
			pr.waitFor(); 
			appName = new String(getOutAndErrStream(pr));
			appName = appName.replace("\n", "");
			Debug.Log(Debug.TRACE, "appName="+appName);
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running androidGetAPKName "+e.toString());
		}
	}
	public void xcodeRunScripts() {
		Process pr;
		try {
			pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_PWD),null,new File("."));
			pr.waitFor(); 
			String androidxPath = new String(getOutAndErrStream(pr));
			androidxPath = androidxPath.replace("\n", "");
			Debug.Log(Debug.TRACE, "xcodeRunScripts current dir "+androidxPath);

			String cpscr = 
					String.format(Config.getStringValue(Config.SWARM_COPY_BUILD_IPA_SCRIPTS), 
							androidxPath, androidxPath+"/"+this.p.BaseName+"/"+this.p.buildDirectory);
			Debug.Log(Debug.TRACE, "xcodeRunScripts Executing "+cpscr);
			pr = Runtime.getRuntime().exec(cpscr, null, new File(androidxPath));
			pr.waitFor(); 
			String ars = new String(getOutAndErrStream(pr));
			ars = ars.replace("\n", "");
			Debug.Log(Debug.TRACE, "xcodeRunScripts cp result "+ars);
			
			String scr2 = 
					String.format(Config.getStringValue(Config.SWARM_XCODE_CREATE_IPA), 
							this.p.BaseNameMinimal);
			Debug.Log(Debug.TRACE, "xcodeRunScripts Executing2 "+scr2+" in "+androidxPath+"/"+this.p.BaseName+"/"+this.p.buildDirectory);
			pr = Runtime.getRuntime().exec(scr2, null, new File(androidxPath+"/"+this.p.BaseName+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			ars = new String(getOutAndErrStream(pr));
			ars = ars.replace("\n", "");
			Debug.Log(Debug.TRACE, "xcodeRunScripts ver2 result "+ars);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.Log(Debug.INFO, "Exception caught running xcodeRunScripts "+e.toString());
		}
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
