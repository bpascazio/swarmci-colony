package com.bytefly.swarm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Version;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

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

	public String getURL(String eurl) {

		boolean updated = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton
														// problem
		BufferedReader in = null;

		try {

			String sstr = java.net.URLEncoder.encode(eurl, "ISO-8859-1");
			String url = eurl;

			Debug.Log(Debug.TRACE, "url=" + url);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

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
			in.close();
			String page = sb.toString();
			Debug.Log(Debug.TRACE, "response=" + page);
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
			String url = Config.getStringValue(Config.SWARM_RAILS_URL) + "/"
					+ entitystr + ".json";

			Debug.Log(Debug.TRACE, "url=" + url);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

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

	public void setEntity(Entity e) {
		// Create a new HttpClient and Post Header
		String entitystr = e.ENTITY_COLLECTION;
		String url = Config.getStringValue(Config.SWARM_RAILS_URL) + "/"
				+ entitystr;
		
		Debug.Log(Debug.TRACE, "url=" + url);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		BufferedReader in = null;
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		if (e instanceof Build) {

			Build b = (Build)e;
			nameValuePairs
					.add(new BasicNameValuePair("build[success]", b.success?"1":"0"));
			nameValuePairs.add(new BasicNameValuePair("build[project_id]",
					""+b.project_id));
			nameValuePairs.add(new BasicNameValuePair("build[user_id]",
					""+b.user_id));
			Debug.Log(Debug.TRACE, "adding entity "+b.project_id+" "+b.user_id+" "+b.success);
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		try {
			// Execute HTTP Post Request
			HttpResponse response;
			response = httpclient.execute(httppost);
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
			Debug.Log(Debug.TRACE, "post response=" + page);
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (ClientProtocolException epx) {
			// TODO Auto-generated catch block
			epx.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();

		}
	}

}
