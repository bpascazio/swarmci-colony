package com.bytefly.swarm.colony.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;

public class Builder {

	public Project p;

	String toList = "null@null.com";
	String toFailList= "null@null.com";

	String tfGroup = "x";
	String tfDist= "y";

	public static final int BUILDER_TYPE_GENERIC = 0;
	public static final int BUILDER_TYPE_XCODE = 1;
	public static final int BUILDER_TYPE_ANDROID = 2;

	public void sendFailureEmail() {

		try {
			String name = p.BaseNameMinimal+"("+p.buildNum+")";
			String owner = "owner";
			String repo = p.BaseNameMinimal;
			String to = toFailList;
			String log = this.p.BaseNameMinimal+this.p.buildNum+".log";
			String cmd = String
					.format(Config
							.getStringValue(Config.SWARM_SEND_FAILURE_EMAIL),
							name, p.buildNum, this.p.BaseName + ".apk", log, owner,
							repo, to, p.commit);
			
			
			Debug.Log(Debug.TRACE, "Executing " + cmd+ " " + p.Repo);
			Process pr = Runtime.getRuntime().exec(cmd, null,
					new File(this.p.BaseName));
			pr.waitFor();
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoGet " + e.toString());
		}
	}

	public void notifyEmail() {
		try {
			Debug.Log(
					Debug.TRACE,
					"getting url "
							+ Config.getStringValue(Config.SWARM_NOTIFY_EMAIL_CMD));
			HttpConnector hc = new HttpConnector();
			String r = hc.getURL(Config
					.getStringValue(Config.SWARM_NOTIFY_EMAIL_CMD));
			Debug.Log(Debug.TRACE, "notifyEmail result=" + r);
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
							+ " " + p.BaseName+ " " + p.Repo);
			Process pr = Runtime.getRuntime().exec(
					Config.getStringValue(Config.SWARM_CLEAN_REPO_CMD) + " "
							+ p.BaseName);
			pr.waitFor();
			Debug.Log(Debug.TRACE, "repoClean result=" + getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoClean " + e.toString());
		}
	}

	public void repoUpdate() {
		try {
			Status.counter_git_updates++;
			Debug.Log(
					Debug.TRACE,
					"Executing "
							+ Config.getStringValue(Config.SWARM_GIT_UPDATE_CMD)+ " " + p.Repo);
			Process pr = Runtime.getRuntime().exec(
					Config.getStringValue(Config.SWARM_GIT_UPDATE_CMD), null,
					new File(this.p.BaseName));
			pr.waitFor();
			String result = getOutAndErrStream(pr).replace("\n", "");
			Debug.Log(Debug.TRACE, "repoUpdate result=" + result);
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoUpdate " + e.toString());
		}
	}

	public void repoClone() {
		try {
			Status.counter_git_clone++;
			Debug.Log(
					Debug.TRACE,
					"Executing "
							+ Config.getStringValue(Config.SWARM_GIT_CLONE_CMD)
							+ " " + p.Repo);
			Process pr = Runtime.getRuntime().exec(
					Config.getStringValue(Config.SWARM_GIT_CLONE_CMD) + " "
							+ p.Repo, null, new File(Config.getProjectDir()));
			pr.waitFor();
			Debug.Log(Debug.TRACE, "repoClone result=" + getOutAndErrStream(pr));
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoGet " + e.toString());
		}
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

	public class ReadXMLFile {

		public void execute(String fname) {

			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();

				DefaultHandler handler = new DefaultHandler() {

					boolean tos = false;
					boolean tof = false;
					boolean tfg = false; //group
					boolean tfd = false; //distribution list

					public void startElement(String uri, String localName,
							String qName, org.xml.sax.Attributes attributes)
							throws SAXException {

						if (qName.equalsIgnoreCase("to_success")) {
							tos = true;
						}

						if (qName.equalsIgnoreCase("to_failure")) {
							tof = true;
						}
						if (qName.equalsIgnoreCase("testflight_token")) {
							tfg = true;
						}

						if (qName.equalsIgnoreCase("testflight_distribution_group")) {
							tfd = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {

					}

					public void characters(char ch[], int start, int length)
							throws SAXException {

						if (tos) {
							tos = false;
							toList = new String(ch, start, length);
						}

						if (tof) {
							tof = false;
							toFailList = new String(ch, start, length);
						}

						if (tfg) {
							tfg = false;
							tfGroup = new String(ch, start, length);
						}

						if (tfd) {
							tfd = false;
							tfDist = new String(ch, start, length);
						}

					}

				};

				saxParser.parse(fname, handler);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void loadSwarmXML() {

		try {

			ReadXMLFile r = new ReadXMLFile();
			r.execute(this.p.BaseName + "/swarm.xml");
			
			toList=toList.replace("\n", "");
			toFailList = toFailList.replace("\n", "");
			tfGroup = tfGroup.replace("\n", "");
			tfDist = tfDist.replace("\n", "");
			
			Debug.Log(Debug.TRACE, "toList " + toList);
			Debug.Log(Debug.TRACE, "toFailList " + toFailList);
			Debug.Log(Debug.TRACE, "tfGroup " + tfGroup);
			Debug.Log(Debug.TRACE, "tfDist " + tfDist);
		} catch (Exception e) {
			Debug.Log(Debug.INFO,
					"Exception caught running repoGet " + e.toString());
		}
	}
}
