package com.bytefly.swarm.cmd.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bytefly.swarm.colony.builders.Builder.ReadXMLFile;
import com.bytefly.swarm.colony.util.Config;

public class SwarmUser {

	public String username = "";
	public String email = "";
	public String password = "";
	public String server = "";
	public int uid = 0;

	public SwarmUser() {
		username = "x";
		email = "x";
		password = "x";
		server = "x";
		uid = 0;
	}
	public static class ReadXMLFile {

		public static boolean success=false;
		
		String lemail="x";
		String lusername="x";
		String lpassword="x";
		String lserver="x";
		
		public void execute(String fname) {

			success=false;
			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();

				DefaultHandler handler = new DefaultHandler() {

					boolean temail = false;
					boolean tusername = false;
					boolean tpassword = false;
					boolean tserver = false;

					public void startElement(String uri, String localName,
							String qName, org.xml.sax.Attributes attributes)
							throws SAXException {

						if (qName.equalsIgnoreCase("email")) {
							temail = true;
						}
						if (qName.equalsIgnoreCase("username")) {
							tusername = true;
						}
						if (qName.equalsIgnoreCase("password")) {
							tpassword = true;
						}
						if (qName.equalsIgnoreCase("server")) {
							tserver = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {

					}

					public void characters(char ch[], int start, int length)
							throws SAXException {

						if (temail) {
							temail = false;
							lemail = new String(ch, start, length);
						}

						if (tusername) {
							tusername = false;
							lusername = new String(ch, start, length);
						}

						if (tpassword) {
							tpassword = false;
							lpassword = new String(ch, start, length);
						}

						if (tserver) {
							tserver = false;
							lserver = new String(ch, start, length);
						}
					}

				};

				saxParser.parse(fname, handler);
				success=true;

			} catch (Exception e) {
//				e.printStackTrace();
			}

		}

	}

	// returns true if file was loaded successfully
	private static ReadXMLFile attemptLoadFromFile() {
		ReadXMLFile r = null;
		try {
			String path = Config.getStringValue(Config.SWARM_COLONY_CONFIG_PATH);
			if (path.equals("")) {
				String userHome = System.getProperty( "user.home" );				
				path = userHome+"/.swarm/swarmcfg.xml";
			}
			r = new ReadXMLFile();
			r.execute(path);
		} catch (Exception e) {
			System.out.print(
					"Exception caught running attemptLoadFromFile " + e.toString());
		}
		return r;
	}

	public static SwarmUser getUserInfo() {
		SwarmUser su = null;
		String username="x";
		String password="x";
		String email="x";
		String server="x";
		ReadXMLFile r = attemptLoadFromFile();
		if (r==null || !r.success) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Username/Email: ");
			username = scanner.nextLine();
			username = username.replace("\n", "");
			email = username;
			System.out.print("Password: ");
			password = scanner.nextLine();
			password = password.replace("\n", "");
			email = username;
//			System.out.print("Server [default ]: ");
//			server = scanner.nextLine();
//			server = server.replace("\n", "");
			server="";
			if (server.equals("")) server = Config.getStringValue(Config.SWARM_RAILS_URL);
			writeToFile(username, email, password, server);
		} else {
			username =  r.lusername;
			password =  r.lpassword;
			email =  r.lemail;
			server =  r.lserver;
		}
		su = new SwarmUser();
		su.email = new String(email);
		su.username = new String(username);
		su.password = new String (password);
		su.server = new String (server);
		return su;
	}
	private static final String dotswarmxml = "<?xml version=\"1.0\"?>\n"
			+ "<swarm>\n"
			+ "\t<email>%s</email>\n"
			+ "\t<username>%s</username>\n"
			+ "\t<password>%s</password>\n"
			+ "\t<server>%s</server>\n"
			+ "</swarm>\n";
	
	private static void writeToFile(String u, String e, String p, String s) {
		String xmlfile = String.format(dotswarmxml, e, u, p, s);
		try {
			
			String userHome = System.getProperty( "user.home" );
//			System.out.print("userHome "+userHome);
			Process pr = Runtime.getRuntime().exec(
					Config.getStringValue(Config.SWARM_MAKE_DOT_SWARM_DIR), null, new File(userHome));
			pr.waitFor();
			String result = getOutAndErrStream(pr).replace("\n", "");
//			System.out.print("resultmk "+result);
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(userHome+"/.swarm/swarmcfg.xml", false));
			bw.write(xmlfile);
			bw.flush();
			bw.close();
			System.out.print("Created local swarm config file.\n");
		} catch (Exception xe) {
			// TODO Auto-generated catch block
			xe.printStackTrace();
		}
	}
	

	protected static String getOutAndErrStream(Process p) {

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
