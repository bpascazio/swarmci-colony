package com.bytefly.swarm.colony.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class Logfile {

	String fname = "";
	String fullname = "";
	String fdir = "";
	BufferedWriter bw;

	public String getFileName() {
		return fname;
	}
	
	public void writeToLog(String lmsg) {
		try {
			bw.write(lmsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopLogFile() {
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		androidUploadBuild();
	}

	public void startLogFile(String name, String dir) {

		fname = name;
		fullname = dir+"/"+name;
		fdir = dir;
		Debug.Log(Debug.TRACE, "starting log file...");

		// Create it and commit it.
		Debug.Log(Debug.TRACE, "creating log file " + fullname + "dir");
		try {
			bw = new BufferedWriter(new FileWriter(fullname, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("\n");
	}
	
	public void androidUploadBuild() {

		try {
			String cmd = 
					String.format(Config.getStringValue(Config.SWARM_UPLOAD_LOGFILE), 
							fullname, fname);
			Debug.Log(Debug.TRACE, "Executing "+cmd);
			Process pr = Runtime.getRuntime().exec(cmd,null,new File("."));
			pr.waitFor(); 
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "Exception caught running androidUploadBuild "+e.toString());
		}
	}

}
