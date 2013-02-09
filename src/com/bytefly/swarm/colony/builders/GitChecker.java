package com.bytefly.swarm.colony.builders;

import java.io.File;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class GitChecker extends Builder {

	public String lastCheckin = null;
	
	public void runAll() {
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
		this.gitChecker();
	}
	
	public void gitChecker() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_GIT_CHECK_CMD)+"");
			Status.counter_git_checks++;
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_GIT_CHECK_CMD),null,new File(this.p.BaseName));
			pr.waitFor(); 
			lastCheckin = new String(getOutAndErrStream(pr));
			Debug.Log(Debug.TRACE, "result="+lastCheckin);
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}
	}

}
