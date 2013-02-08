package com.bytefly.swarm.colony.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.omg.CORBA.portable.InputStream;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class XCodeBuilder extends Builder {

	public void runAll() {
		this.repoClean();
		this.repoGet();
		this.xcodeBuild();
		this.notifyEmail();
	}
	
	public void xcodeBuild() {
		try {
			Debug.Log(Debug.TRACE, "Executing "+Config.getStringValue(Config.SWARM_XCODE_BUILD_CMD));
			Process pr = Runtime.getRuntime().exec(Config.getStringValue(Config.SWARM_XCODE_BUILD_CMD),null,new File(this.p.BaseName));
			pr.waitFor(); 
			Debug.Log(Debug.TRACE, "result="+getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running repoGet "+e.toString());
		}
	}

}
