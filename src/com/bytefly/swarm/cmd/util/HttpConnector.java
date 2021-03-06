package com.bytefly.swarm.cmd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.Version;

//
//This class provides a simple stable HTTP communication to fetch an entity list.
//

public class HttpConnector {

	public final static int ERROR_CODE_COMMUNICATON_PROBLEM = -1;
	public final static int ERROR_CODE_SUCCESS = 0;
	public final static int ERROR_CODE_CITY_NOT_FOUND = 1;

	public String tempstr = "";
	public String infostr = "";
	public int error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume
																// communication
	static HttpClient httpclient = null; // problem
	static BasicHttpContext mHttpContext = new BasicHttpContext();
	CookieStore mCookieStore = new BasicCookieStore();

	public String getURL(String eurl) {

		boolean updated = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communication
														// problem
		BufferedReader in = null;

		try {

			String url = eurl;

			if (httpclient == null)
				httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "useragent=" + useragent);

			// execute the http GET
			HttpResponse response = httpclient.execute(request, mHttpContext);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			return page;

		} catch (Exception e) {

			//
			// Note that this exception may be json parsing related or HTTP
			// related.
			// That is why we assume a server communication error.
			//
			e.printStackTrace();

		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
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
		Vector<Entity> c = null;

		try {

			String entitystr = java.net.URLEncoder.encode(entity, "ISO-8859-1");
			String url = "http://www.swarmci.com" + "/" + entitystr + ".json";

			Debug.Log(Debug.TRACE, "url=" + url);

			if (httpclient == null)
				httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "useragent=" + useragent);

			// execute the http GET
			HttpResponse response = httpclient.execute(request, mHttpContext);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
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
					p.UserId = Integer.valueOf(o.getString("user_id"));
					p.ProjectId = Integer.valueOf(o.getString("id"));
					c.add(p);
				}
			}

		} catch (Exception e) {

			//
			// Note that this exception may be json parsing related or HTTP
			// related.
			// That is why we assume a server communication error.
			//
			e.printStackTrace();

		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return c;
	}

	public int checkConnection(String sserver, String semail, String spassword) {

		int userid = 0; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM;	// assume communication
														// problem
		BufferedReader in = null;

		try {
			String url = String.format(Config
					.getStringValue(Config.SWARM_COLONY_AUTHENTICATION_V1),
					sserver, semail, spassword);

			if (httpclient == null)
				httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);

			// execute the http GET
			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
			HttpResponse response = httpclient.execute(request, mHttpContext);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();

			url = String.format(Config
					.getStringValue(Config.SWARM_COLONY_AUTHENTICATION_TOKEN),
					sserver, semail, spassword);

			if (httpclient == null)
				httpclient = new DefaultHttpClient();
			request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			useragent = "swarm" + Version.getVersion() + " "
					+ Version.getBuildNum();
			request.setHeader("User-Agent", useragent);

			// execute the http GET
			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
			response = httpclient.execute(request, mHttpContext);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			sb = new StringBuffer("");
			line = "";
			NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			page = sb.toString();
			page = page.replace("\n", "");
			page = page.replace(" ", "");
			if (!page.equals("")) {
				userid = Integer.valueOf(page);
			}

		} catch (Exception e) {

			//
			// Note that this exception may be json parsing related or HTTP
			// related.
			// That is why we assume a server communication error.
			//
			Debug.Log(Debug.TRACE, "exception=" + e);
			e.printStackTrace();

		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return userid;
	}

	public void setEntity(String sserver, Entity e) {
		
		// Create a new HttpClient and Post Header
		String entitystr = e.ENTITY_COLLECTION;
		String url = "http://" + sserver + "/" + entitystr;

		if (httpclient == null)
			httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		BufferedReader in = null;
		
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		if (e instanceof Project) {

			Project b = (Project) e;
			nameValuePairs.add(new BasicNameValuePair("project[name]", b.Name));
			nameValuePairs.add(new BasicNameValuePair("project[repo]", ""
					+ b.Repo));
			nameValuePairs.add(new BasicNameValuePair("project[user_id]", ""
					+ b.UserId));
			nameValuePairs.add(new BasicNameValuePair("project[builder]", ""
					+ b.Builder));
		}

		if (e instanceof Build) {

			Build b = (Build) e;
			nameValuePairs.add(new BasicNameValuePair("build[success]",
					b.success ? "1" : "0"));
			nameValuePairs.add(new BasicNameValuePair("build[project_id]", ""
					+ b.project_id));
			nameValuePairs.add(new BasicNameValuePair("build[user_id]", ""
					+ b.user_id));
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (Exception ex) {
			Debug.Log(Debug.TRACE, "exception=" + ex);
		}

		try {
			// Execute HTTP Post Request
			HttpResponse response;

			response = httpclient.execute(httppost, mHttpContext);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Debug.Log(Debug.TRACE, "exception=" + ex);
				}
			}
		} catch (ClientProtocolException epx) {
			epx.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();

		}
	}

}
