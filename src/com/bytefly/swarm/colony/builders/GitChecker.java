package com.bytefly.swarm.colony.builders;

import java.io.File;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class GitChecker extends Builder {

	public String lastCheckin = null;
	
	public void runAll() {
		this.repoClean();
		this.repoGet();
		this.gitChecker();
	}
	
	public void gitChecker() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_GIT_CHECK_CMD)+"");
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_GIT_CHECK_CMD),null,new File(this.p.BaseName));
			pr.waitFor(); 
			lastCheckin = new String(getOutAndErrStream(pr));
			Debug.Log(Debug.TRACE, "result="+lastCheckin);
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}
	}

}
