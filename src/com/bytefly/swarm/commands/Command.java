package com.bytefly.swarm.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.util.Debug;

public class Command {

	public boolean checkForGit() {

		boolean gitFound = false;
		System.out.print("Determining name of repository.\n");
		try {
		Process pr = Runtime.getRuntime().exec(
				"find . -name .git -print -quit", null, new File("."));
		pr.waitFor();
		String result = getOutAndErrStream(pr).replace("\n", "");
		if (!result.equals("")) {gitFound=true;}
		} catch (Exception e) {
			if (Debug.verbose)System.out.print("git search exception: "+e);
		}
		if (!gitFound) {
			System.out.print("Error: you are not in the root of a git repository. ");
		}
		return gitFound;
	}

	protected String getOutAndErrStream(Process p) {

		StringBuffer cmd_out = new StringBuffer("");
		if (p != null) {
			BufferedReader is = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String buf = "";
			try {
				while ((buf = is.readLine()) != null) {
					cmd_out.append(buf);
					cmd_out.append(System.getProperty("line.separator"));
				}
				is.close();
				is = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				while ((buf = is.readLine()) != null) {
					cmd_out.append(buf);
					cmd_out.append("\n");
				}
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cmd_out.toString();
	}
}
