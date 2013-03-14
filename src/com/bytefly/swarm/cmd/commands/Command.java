package com.bytefly.swarm.cmd.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class Command {

	private static final String swarmxml = "<?xml version=\"1.0\"?>\n"
			+ "<swarm>\n"
			+ "\t<!-- comma delimited list of who you email the build to on success -->\n"
			+ "\t<to_success>%s</to_success>\n"
			+ "\t<!-- comma delimited list of who you email to on build failures -->\n"
			+ "\t<to_failure>%s</to_failure>\n"
			+ "\t<!-- testflight api token-->\n"
			+ "\t<testflight_api_token></testflight_api_token>\n"
			+ "\t<!-- testflight team token-->\n"
			+ "\t<testflight_team_token></testflight_team_token>\n"
			+ "\t<!-- testflight distribution group -->\n"
			+ "\t<testflight_distribution_group></testflight_distribution_group>\n"
			+ "\t<email_git_info>true</email_git_info>\n"
			+ "</swarm>\n";

	public boolean checkForGit() {

		boolean gitFound = false;
		System.out.print("Determining name of repository.\n");
		try {
			Process pr = Runtime.getRuntime().exec(
					"find . -name .git -print -quit", null, new File("."));
			pr.waitFor();
			String result = getOutAndErrStream(pr).replace("\n", "");
			if (!result.equals("")) {
				gitFound = true;
			}
		} catch (Exception e) {
			System.out.print("git search exception: " + e);
		}
		if (!gitFound) {
			System.out
					.print("Error: you are not in the root of a git repository. ");
		}
		return gitFound;
	}

	public boolean checkForSwarmXML() {

		boolean sxmlFound = false;
		boolean sxmlok = false;
		boolean gitadd = false;
		boolean gitcommit = false;
		boolean gitpush = false;
		boolean success = false;
		System.out.print("Looking for swarm.xml...");
		try {
			Process pr = Runtime.getRuntime().exec(
					"find . -name swarm.xml -print -quit", null, new File("."));
			pr.waitFor();
			String result = getOutAndErrStream(pr).replace("\n", "");
			if (!result.equals("")) {
				System.out.print("found.\n");
				sxmlFound = true;
				success = sxmlok = true;
			}
		} catch (Exception e) {
				System.out.print("swarm.xml search exception: " + e);
		}
		if (!sxmlFound) {
			// Create it and commit it.
			Scanner scanner = new Scanner(System.in);
			System.out.print("\nswarm.xml not found - create and push? (Y/n): ");
			String resp = scanner.nextLine();
			resp = resp.replace("\n", "");
			if (resp.equals("n")||resp.equals("N")) {
				sxmlok = false;
			}
			
			SwarmUser u = SwarmUser.getUserInfo();
			String xmlfile = String.format(swarmxml, u.email, u.email);
			try {
				BufferedWriter bw;
				bw = new BufferedWriter(new FileWriter("swarm.xml", true));
				bw.write(xmlfile);
				bw.flush();
				sxmlok = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (sxmlok) {
				System.out.print("Git adding");
				try {
					Process pr = Runtime.getRuntime().exec("git add swarm.xml",
							null, new File("."));
					pr.waitFor();
					String result = getOutAndErrStream(pr).replace("\n", "");
					if (result.equals("")) {			
						System.out.print("...");
						gitadd = true;
					}
				} catch (Exception e) {
					System.out.print("add search exception: " + e);
				}
			}

			if (gitadd) {
				System.out.print("commiting");
				try {
					Process pr = Runtime.getRuntime().exec("git commit -m added swarm.xml",
							null, new File("."));
					pr.waitFor();
					String result = getOutAndErrStream(pr).replace("\n", "");
//					if (result.equals("")) {
						System.out.print("...");
						gitcommit = true;
//					}
				} catch (Exception e) {
					System.out.print("commit search exception: " + e);
				}
			}

			if (gitcommit) {
				System.out.print("pushing\n");
				try {
					Process pr = Runtime.getRuntime().exec("git push",
							null, new File("."));
					pr.waitFor();
					String result = getOutAndErrStream(pr).replace("\n", "");
//					if (result.equals("")) {
						gitpush = true;
						success = true;
//					}
				} catch (Exception e) {
					System.out.print("push exception: " + e);
				}
			}
			System.out.print("\n");

		}
		return success;
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
