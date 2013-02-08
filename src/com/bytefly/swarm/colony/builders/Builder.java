package com.bytefly.swarm.colony.builders;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.common.util.HttpConnector;

public class Builder {

	public Project p;

	public static final int BUILDER_TYPE_GENERIC = 0;
	public static final int BUILDER_TYPE_XCODE = 1;
	public static final int BUILDER_TYPE_ANDROID = 2;

	public void notifyEmail() {
		try {
			Debug.Log(
					Debug.TRACE,
					"getting url "
							+ Config.getStringValue(Config.SWARM_NOTIFY_EMAIL_CMD));
			HttpConnector hc = new HttpConnector();
			String r = hc.getURL(Config.getStringValue(Config.SWARM_NOTIFY_EMAIL_CMD));
			Debug.Log(Debug.TRACE, "result="+r);
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running notifyEmail " + e.toString());
		}
	}

	public void repoClean() {
		try {
			Debug.Log(
					Debug.TRACE,
					"Executing "
							+ Config.getStringValue(Config.SWARM_CLEAN_REPO_CMD)
							+ " " + p.BaseName);
			Process pr = Runtime.getRuntime().exec(
					Config.getStringValue(Config.SWARM_CLEAN_REPO_CMD) + " "
							+ p.BaseName);
			pr.waitFor(); 
			Debug.Log(Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoClean " + e.toString());
		}
	}

	public void repoGet() {
		try {
			Debug.Log(
					Debug.TRACE,
					"Executing "
							+ Config.getStringValue(Config.SWARM_GIT_CLONE_CMD)
							+ " " + p.Repo);
			Process pr = Runtime.getRuntime().exec(
					Config.getStringValue(Config.SWARM_GIT_CLONE_CMD) + " "
							+ p.Repo);
			pr.waitFor(); 
			Debug.Log(Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoGet " + e.toString());
		}
	}

	protected String getOutAndErrStream(Process p){

		StringBuffer cmd_out = new StringBuffer("");
		if(p != null){
			BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String buf = "";
			try{
				while((buf = is.readLine()) != null){
					cmd_out.append(buf);
					cmd_out.append (System.getProperty("line.separator"));
				}
				is.close();
				is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while((buf = is.readLine()) != null){
					cmd_out.append(buf);
					cmd_out.append("\n");
				}
				is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
		return cmd_out.toString();
	}
}
