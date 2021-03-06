package com.bytefly.swarm.colony.builders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
			Debug.Log(p.Name, Debug.TRACE, "Git repo exists");
			this.repoUpdate();
		} else {
			this.repoClone();
		}
		androidRemoveBinDirs();
		p.logFile = new Logfile();
		p.logFile.startLogFile(p.BaseNameMinimal+p.buildNum+".log", p.BaseName);
		androidSetSDK();
		androidVerifyCreateBuildXml();
		androidRunScripts();
		andoidBuild();
		apkName="";
		androidGetAPKName();
		Build bd = new Build();
		bd.user_id = p.UserId;
		bd.project_id = p.ProjectId;
		bd.success = false;
		if (apkName.equals("")) {
			Debug.Log(p.Name, Debug.TRACE, "no apk found");			
			Status.counter_builds_failure++;
			p.reason="Failed%20during%20compile.";
			p.logFile.stopLogFile();
			sendFailureEmail();
		} else {
			Debug.Log(p.Name, Debug.TRACE, "apk found uploading and emailing");						
			Status.counter_builds_success++;
			androidUploadBuild();
			p.logFile.stopLogFile();
			androidSendEmail();
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
		bd.bldtype = Build.BUILD_TYPE_ANDROID;
		HttpConnector h = new HttpConnector();
		h.setEntity(bd);
	}
	
	public void androidRemoveBinDirs() {
		try {
			Debug.Log(p.Name, Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_CLEARN_CMD)+" in directory "+this.p.BaseName+"/"+this.p.buildDirectory);
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_CLEARN_CMD),null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			Debug.Log(p.Name, Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidRemoveBinDirs "+e.toString());
		}		
	}
	
	public void androidSetSDK() {
		try {
			String lfile="sdk.dir="+Config.getAndroidSDK()+"\n";
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(this.p.BaseName+"/"+this.p.buildDirectory+"local.properties", true));
			bw.write(lfile);
			bw.flush();
			Debug.Log(p.Name, Debug.TRACE, "android sdk set to "+Config.getAndroidSDK());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidSetSDK "+e.toString());
		}

	}
	
	public void androidVerifyCreateBuildXml() {
		Debug.Log(p.Name, Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_FIND_BUILDXML)+"");
		Process pr;
		try {
			pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_FIND_BUILDXML),null,new File(this.p.BaseName));
			pr.waitFor(); 
			String androidxPath = new String(getOutAndErrStream(pr));
			androidxPath = androidxPath.replace("\n", "");
			Debug.Log(p.Name, Debug.TRACE, "androidxPath "+androidxPath);
			if (androidxPath.equals("")) {
				String createbxml = Config.getAndroidSDK()+"/"+Config.getStringValue(Config.SWARM_ANDROID_GENERATE_BUILDXML);
				Debug.Log(p.Name, Debug.TRACE, "Executing "+createbxml);
				pr = Runtime.getRuntime().exec(createbxml,null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
				pr.waitFor(); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidVerifyCreateBuildXml "+e.toString());
		}
	}
	
	public void androidRunScripts() {
		Process pr;
		try {
			pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_PWD),null,new File("."));
			pr.waitFor(); 
			String androidxPath = new String(getOutAndErrStream(pr));
			androidxPath = androidxPath.replace("\n", "");
			Debug.Log(p.Name, Debug.TRACE, "androidRunScripts current dir "+androidxPath);

			String cpscr = 
					String.format(Config.getStringValue(Config.SWARM_COPY_ANDROID_SCRIPTS), 
							androidxPath, androidxPath+"/"+this.p.BaseName+"/"+this.p.buildDirectory);
			Debug.Log(p.Name, Debug.TRACE, "androidRunScripts Executing "+cpscr);
			pr = Runtime.getRuntime().exec(cpscr, null, new File(androidxPath));
			pr.waitFor(); 
			String ars = new String(getOutAndErrStream(pr));
			ars = ars.replace("\n", "");
			Debug.Log(p.Name, Debug.TRACE, "androidRunScripts cp result "+ars);
			
			String scr2 = 
					String.format(Config.getStringValue(Config.SWARM_ANDROID_UPDATE_VERSION), 
							this.p.buildNum);
			Debug.Log(p.Name, Debug.TRACE, "androidRunScripts Executing2 "+scr2+" in "+androidxPath+"/"+this.p.BaseName+"/"+this.p.buildDirectory);
			pr = Runtime.getRuntime().exec(scr2, null, new File(androidxPath+"/"+this.p.BaseName+"/"+this.p.buildDirectory));
			pr.waitFor(); 
			ars = new String(getOutAndErrStream(pr));
			ars = ars.replace("\n", "");
			Debug.Log(p.Name, Debug.TRACE, "androidRunScripts ver2 result "+ars);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidRunScripts "+e.toString());
		}
	}
	
	public void andoidBuild() {
		try {
			String target="release";
			if (p.debug)target="debug";
			Debug.Log(p.Name, Debug.DEBUG, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_BUILD_CMD)+" "+target+" in directory "+this.p.BaseName+"/"+this.p.buildDirectory);
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_BUILD_CMD)+" "+target,null,new File(this.p.BaseName+"/"+this.p.buildDirectory));
		
			  final InputStream stream = pr.getInputStream();
			  new Thread(new Runnable() {
			    public void run() {
			      BufferedReader reader = null;
			      try {
			        reader = new BufferedReader(new InputStreamReader(stream));
			        String line = null;
			        while ((line = reader.readLine()) != null) {
			          System.out.println(line);
			        }
			      } catch (Exception e) {
			        // TODO
			      } finally {
			        if (reader != null) {
			          try {
			            reader.close();
			          } catch (IOException e) {
			            // ignore
			          }
			        }
			      }
			    }
			  }).start();
			
			
			pr.waitFor(); 
			String result = getOutAndErrStream(pr);
			p.logFile.writeToLog(result);
			Debug.Log(p.Name, Debug.TRACE, "result="+result);
		} catch (Exception e) {
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidRemoveBinDirs "+e.toString());
		}
	}
	
	public void androidGetAPKName() {
		
		try {
			Debug.Log(p.Name, Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_ANDROID_APP_NAME));
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_ANDROID_APP_NAME),null,new File(this.p.BaseName));
			pr.waitFor(); 
			apkName = new String(getOutAndErrStream(pr));
			apkName = apkName.replace("\n", "");
			Debug.Log(p.Name, Debug.TRACE, "apkName="+apkName);
		} catch (Exception e) {
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidGetAPKName "+e.toString());
		}
	}
	
	public void androidUploadBuild() {

		try {
			String cmd = 
					String.format(Config.getStringValue(Config.SWARM_ANDROID_UPLOAD_APK), 
							this.apkName, this.p.BaseNameMinimal+this.p.buildNum+".apk");
			Debug.Log(p.Name, Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File(this.p.BaseName));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidUploadBuild "+e.toString());
		}
	}
	
	public void androidSendEmail() {

		try {
			String name = p.BaseNameMinimal+"-"+p.Version+"."+p.buildNum;
			String owner = p.Owner;
			String repo = p.BaseNameMinimal;
			String builder = p.Builder+"-"+Config.getColonyUUID();
			String to = this.toList;
			String log = "none";		
			String commit = "none";
			if (emailGit.equals("true")) {
				commit=p.commit;
			}
			if (p.logFile!=null) {
				log = this.p.BaseNameMinimal+this.p.buildNum+".log";
			}
			String cmd = 
					String.format(Config.getStringValue(Config.SWARM_ANDROID_SEND_EMAIL_APK), 
							name, p.buildNum, this.p.BaseNameMinimal+this.p.buildNum+".apk", log, owner, repo, to, commit, builder);
			Debug.Log(p.Name, Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File(this.p.BaseName));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(p.Name, Debug.INFO, "Exception caught running androidSendEmail "+e.toString());
		}	
	}
}
