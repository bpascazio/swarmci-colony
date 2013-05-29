package com.bytefly.swarm.cmd.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bytefly.swarm.colony.util.Config;

public class SwarmUser {

	public String username = "";
	public String email = "";
	public String password = "";
	public String server = "";
	public int uid = 0;
	private static byte[] linebreak = {}; // Remove Base64 encoder default
											// linebreak
	private static String secret = "tvnw63ufg9gh5392"; // secret key length must
														// be 16
	private static SecretKey key;
	private static Cipher cipher;
	private static Base64 coder;

	public SwarmUser() {
		username = "x";
		email = "x";
		password = "x";
		server = "x";
		uid = 0;
		try {
			key = new SecretKeySpec(secret.getBytes(), "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			coder = new Base64(32, linebreak, true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public String encrypt(String plainText) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return new String(coder.encode(cipherText));
	}

	public String decrypt(String codedText) throws Exception {
		byte[] encypted = coder.decode(codedText.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encypted);
		return new String(decrypted);
	}

	public static class ReadXMLFile {

		public static boolean success = false;

		String lemail = "x";
		String lusername = "x";
		String lpassword = "x";
		String lserver = "x";

		public void execute(String fname) {

			success = false;
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
				success = true;

			} catch (Exception e) {
				// e.printStackTrace();
			}

		}

	}

	// returns true if file was loaded successfully
	private static ReadXMLFile attemptLoadFromFile() {
		ReadXMLFile r = null;
		try {
			String path = Config
					.getStringValue(Config.SWARM_COLONY_CONFIG_PATH);
			if (path.equals("")) {
				String homeDir = System.getenv("HOMEPATH");
				String userHome = System.getProperty("user.home");
				if (homeDir != null && homeDir.equals("") == false) {
					path = System.getenv("HOMEDRIVE") + "\\swarmcfg.xml";
				} else {
					path = userHome + "/.swarm/swarmcfg.xml";
				}
			}
			r = new ReadXMLFile();
			r.execute(path);

		} catch (Exception e) {
			System.out.print("Exception caught running attemptLoadFromFile "
					+ e.toString());
		}
		return r;
	}

	class EraserThread implements Runnable {
		private boolean stop;

		/**
		 * @param The
		 *            prompt displayed to the user
		 */
		public EraserThread(String prompt) {
			System.out.print(prompt);
		}

		/**
		 * Begin masking...display asterisks (*)
		 */
		public void run() {
			stop = true;
			while (stop) {
				System.out.print("\010*");
				try {
					Thread.currentThread().sleep(1);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}

		/**
		 * Instruct the thread to stop masking
		 */
		public void stopMasking() {
			this.stop = false;
		}
	}

	public String readPassword(String prompt) {
		EraserThread et = new EraserThread(prompt);
		Thread mask = new Thread(et);
		mask.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String password = "";

		try {
			password = in.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// stop masking
		et.stopMasking();
		// return the password entered by the user
		return password;
	}

	public static SwarmUser getUserInfo() {
		SwarmUser su = new SwarmUser();
		String username = "x";
		String password = "x";
		String email = "x";
		String server = "x";
		ReadXMLFile r = attemptLoadFromFile();
		if (r == null || !r.success) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Username/Email: ");
			username = scanner.nextLine();
			username = username.replace("\n", "");
			email = username;
			System.out.print("Password: ");
			password = new String(System.console().readPassword());
			password = password.replace("\n", "");

			email = username;
			server = "";
			if (server.equals(""))
				server = Config.getStringValue(Config.SWARM_RAILS_URL);
			writeToFile(username, email, password, server);
		} else {
			username = r.lusername;
			try {
				password = su.decrypt(r.lpassword);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			email = r.lemail;
			server = r.lserver;
		}
		su.email = new String(email);
		su.username = new String(username);
		su.password = new String(password);
		su.server = new String(server);

		return su;
	}

	private static final String dotswarmxml = "<?xml version=\"1.0\"?>\n"
			+ "<swarm>\n" + "\t<email>%s</email>\n"
			+ "\t<username>%s</username>\n" + "\t<password>%s</password>\n"
			+ "\t<server>%s</server>\n" + "</swarm>\n";

	private static void writeToFile(String u, String e, String p, String s) {
		try {
			SwarmUser su = new SwarmUser();
			String xmlfile = String.format(dotswarmxml, e, u, su.encrypt(p), s);

			String userHome = System.getProperty("user.home");
			String homeDir = System.getenv("HOMEPATH");
			if (homeDir == null || homeDir.equals("") == true) {
				Process pr = Runtime.getRuntime().exec(
						Config.getStringValue(Config.SWARM_MAKE_DOT_SWARM_DIR),
						null, new File(userHome));
				pr.waitFor();
				String result = getOutAndErrStream(pr).replace("\n", "");
			}
			String path = userHome + "/.swarm/swarmcfg.xml";
			if (homeDir != null && homeDir.equals("") == false) {
				path = System.getenv("HOMEDRIVE") + "\\swarmcfg.xml";
			}
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(path, false));
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
