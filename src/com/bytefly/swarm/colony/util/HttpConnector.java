package com.bytefly.swarm.colony.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;

// 
// This class provides a simple stable HTTP communication to fetch an entity list.
//

public class HttpConnector {

	public final static int ERROR_CODE_COMMUNICATON_PROBLEM = -1;
	public final static int ERROR_CODE_SUCCESS = 0;
	public final static int ERROR_CODE_CITY_NOT_FOUND = 1;

	public String tempstr = "";
	public String infostr = "";
	public int error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume
																// communicaton
																// problem

	private static HttpClient client = null;

	public static DefaultHttpClient getThreadSafeClient() {

		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,

		mgr.getSchemeRegistry()), params);
		return client;
	}

	public boolean checkConnection(String semail, String spassword) {

		boolean connected = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton
														// problem
		BufferedReader in = null;
		HttpGet request = null;
		try {
			String url = String.format(Config
					.getStringValue(Config.SWARM_COLONY_AUTHENTICATION_V1),
					Config.getStringValue(Config.SWARM_RAILS_URL), semail,
					spassword);

			Debug.Log(Debug.TRACE, "***SECURITY**** url=" + url);

			client = getThreadSafeClient();
			request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "****SECURITY****  useragent=" + useragent);

			// execute the http GET
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			String page = sb.toString();
			// Debug.Log(Debug.TRACE, "***SECURITY****  response=" + page);
			// Debug.Log(Debug.TRACE, "***SECURITY****  response=" +
			// response.toString());
			Debug.Log(
					Debug.TRACE,
					"***SECURITY****  cookie="
							+ response.getHeaders("Set-Cookie")[0].toString());

			connected = true;

		} catch (Exception e) {

			//
			// Note that this exception may be json parsing related or HTTP
			// related.
			// That is why we assume a server communication error.
			//
			Debug.Log(Debug.INFO, "checkConnection X " + e);

		} finally {

			if (in != null) {
				try {
					in.close();
					request.releaseConnection();
				} catch (IOException e) {

					Debug.Log(Debug.INFO, "checkConnection X " + e);

				}
			}

		}

		return connected;
	}

	public String getURL(String eurl) {

		boolean updated = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton
														// problem
		BufferedReader in = null;
		HttpGet request = null;
		try {

			String sstr = java.net.URLEncoder.encode(eurl, "ISO-8859-1");
			String url = eurl;

			Debug.Log(Debug.TRACE, "url=" + url);

			if (client == null)
				client = getThreadSafeClient();
			request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "useragent=" + useragent);

			// execute the http GET
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			String page = sb.toString();
			Debug.Log(Debug.TRACE, "response=" + page);
			return page;

		} catch (Exception e) {

			//
			// Note that this exception may be json parsing related or HTTP
			// related.
			// That is why we assume a server communication error.
			//
			Debug.Log(Debug.INFO, "getURL X " + e);

		} finally {

			if (in != null) {
				try {
					in.close();
					request.releaseConnection();
				} catch (IOException e) {
					Debug.Log(Debug.INFO, "getURL X " + e);
				}
			}

		}

		return null;
	}

	public Vector<Entity> getEntityList(String entity) {

		boolean updated = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton
														// problem
		BufferedReader in = null;
		Vector<Entity> c = new Vector<Entity>();
		HttpGet request = null;

		try {

			String entitystr = java.net.URLEncoder.encode(entity, "ISO-8859-1");
			String url = "http://"
					+ Config.getStringValue(Config.SWARM_RAILS_URL) + "/"
					+ entitystr + ".json";

			Debug.Log(Debug.TRACE, "url=" + url);

			if (client == null)
				client = getThreadSafeClient();
			request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "useragent=" + useragent);

			// execute the http GET
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			String page = sb.toString();
			Debug.Log(Debug.TRACE, "response=" + page);
			//
			// Note that the reponse is JSON.
			//

			JSONTokener tokener = new JSONTokener(page);
			JSONArray res = (JSONArray) tokener.nextValue();
			Debug.Log(Debug.DEBUG, "entity list size=" + res.size());
			if (entity.equals(new Project().ENTITY_COLLECTION)) {
				c = new Vector<Entity>();
				for (int i = 0; i < res.size(); i++) {
					JSONObject o = (JSONObject) res.get(i);
					Project p = new Project();
					p.Name = o.getString("name");
					p.Repo = o.getString("repo");
					p.UserId = o.getInt("user_id");
					p.ProjectId = o.getInt("id");
					p.buildState = o.getInt("state");
					p.buildTrigger =  o.getInt("trigger");
					try {
					p.buildNum =  o.getInt("bldnum");
					} catch (Exception e) {
						p.buildNum = 0;
					}
					p.Builder =  o.getString("builder");
					c.add(p);
				}
			}
			if (entity.equals(new Build().ENTITY_COLLECTION)) {
				c = new Vector<Entity>();
				for (int i = 0; i < res.size(); i++) {
					JSONObject o = (JSONObject) res.get(i);
					Build b = new Build();
					b.created_at = o.getString("created_at");
					b.project_id = Integer.valueOf(o.getString("project_id"));
					c.add(b);
				}
			}

		} catch (Exception e) {

			//
			// Note that this exception may be json parsing related or HTTP
			// related.
			// That is why we assume a server communication error.
			//
			Debug.Log(Debug.INFO, "getEntityList X " + e);
			return null;

		} finally {

			if (in != null) {
				try {
					in.close();
					request.releaseConnection();
				} catch (IOException e) {
					Debug.Log(Debug.INFO, "getEntityList X " + e);
				}
			}

		}

		return c;
	}

	public void setEntity(Entity e) {

		try {

			// Create a new HttpClient and Post Header
			String entitystr = e.ENTITY_COLLECTION;
			String url = "http://"
					+ Config.getStringValue(Config.SWARM_RAILS_URL) + "/"
					+ entitystr;

			Debug.Log(Debug.TRACE, "url=" + url);

			if (client == null)
				client = getThreadSafeClient();
			HttpPost httppost = new HttpPost(url);
			BufferedReader in = null;
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			if (e instanceof Build) {

				Build b = (Build) e;
				nameValuePairs.add(new BasicNameValuePair("build[success]",
						b.success ? "1" : "0"));
				nameValuePairs.add(new BasicNameValuePair("build[project_id]",
						"" + b.project_id));
				nameValuePairs.add(new BasicNameValuePair("build[user_id]", ""
						+ b.user_id));
				nameValuePairs.add(new BasicNameValuePair("build[info]", ""
						+ b.info));
				nameValuePairs.add(new BasicNameValuePair("build[logs]", ""
						+ b.logs));
				nameValuePairs.add(new BasicNameValuePair("build[project_name]", ""
						+ b.project_name));
				nameValuePairs.add(new BasicNameValuePair("build[bldnum]", ""
						+ b.bldnum));
				Debug.Log(Debug.TRACE, "adding entity " + b.project_id + " "
						+ b.user_id + " " + b.success);
			}
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				Debug.Log(Debug.INFO, "setEntity X " + ex);
			}

			try {
				// Execute HTTP Post Request
				HttpResponse response;
				response = client.execute(httppost);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				String page = sb.toString();
				Debug.Log(Debug.TRACE, "post response=" + page);
				if (in != null) {
					try {
						in.close();
						httppost.releaseConnection();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			} catch (ClientProtocolException epx) {
				// TODO Auto-generated catch block
				Debug.Log(Debug.INFO, "setEntity X " + epx);
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				Debug.Log(Debug.INFO, "setEntity X " + ioe);

			}

		} catch (Exception excc) {
			Debug.Log(Debug.INFO, "setEntity X " + excc);
		}
	}


	public boolean updateEntity(Entity e, int id) {

		boolean success=false;
		try {

			// Create a new HttpClient and Post Header
			String entitystr = e.ENTITY_COLLECTION.toLowerCase();
			String surl = "http://"
					+ Config.getStringValue(Config.SWARM_RAILS_URL) + "/"
					+ entitystr + "/" + id;

			Debug.Log(Debug.TRACE, "url=" + surl);

			if (client == null)
				client = getThreadSafeClient();

			HttpPut put= new HttpPut(surl);
			BufferedReader in = null;
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			if (e instanceof Project) {

				Project p = (Project) e;
				nameValuePairs.add(new BasicNameValuePair("project[bldnum]",
						"" + p.buildNum));
				nameValuePairs.add(new BasicNameValuePair("project[state]",
						"" + p.buildState));
				nameValuePairs.add(new BasicNameValuePair("project[trigger]",
						"" + p.buildTrigger));
				nameValuePairs.add(new BasicNameValuePair("project[builder]",
						"" + p.Builder));
				nameValuePairs.add(new BasicNameValuePair("project[bldtype]",
						"" + p.bldtype));
				Debug.Log(Debug.TRACE, "updating entity " + p.Name);
			}
			try {
				put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				Debug.Log(Debug.INFO, "setEntity X " + ex);
			}

			try {
				// Execute HTTP Post Request
				HttpResponse response;
				response = client.execute(put);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				String page = sb.toString();
				Debug.Log(Debug.TRACE, "putresponse=" + page);
				Debug.Log(Debug.TRACE, "putresponse=" + response.toString());
				StatusLine resp=response.getStatusLine();
				Debug.Log(Debug.TRACE, "putstatus =" + resp);
				if (in != null) {
					try {
						in.close();
						put.releaseConnection();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				if (resp.toString().equals("HTTP/1.1 302 Found")) {
					success=true;
				}
			} catch (ClientProtocolException epx) {
				// TODO Auto-generated catch block
				Debug.Log(Debug.INFO, "setEntity X " + epx);
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				Debug.Log(Debug.INFO, "setEntity X " + ioe);

			}

		} catch (Exception excc) {
			Debug.Log(Debug.INFO, "setEntity X " + excc);
		}
		return success;
	}

}
