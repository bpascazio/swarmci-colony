package com.bytefly.swarm.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import com.bytefly.swarm.colony.builders.Builder;
import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Version;
import com.bytefly.swarm.common.util.Config;
import com.bytefly.swarm.common.util.Debug;
import com.sun.tools.javac.code.Attribute.Array;

// 
// This class provides a simple stable HTTP communication to fetch an entity list.
//

public class HttpConnector {

	public final static int ERROR_CODE_COMMUNICATON_PROBLEM = -1;
	public final static int ERROR_CODE_SUCCESS 			  	= 0;
	public final static int ERROR_CODE_CITY_NOT_FOUND	    = 1;
	
	public String tempstr = "";
	public String infostr = "";
	public int error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton problem

	
	public String getURL(String eurl) {

		boolean updated = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton problem
		BufferedReader in = null;
		
		try {

			String sstr = java.net.URLEncoder.encode(eurl, "ISO-8859-1");
			String url = eurl;

			Debug.Log(Debug.TRACE, "url=" + url);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm"
					+ Version.getVersion() + " " + Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "useragent=" + useragent);

			// execute the http GET
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
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
			// Note that this exception may be json parsing related or HTTP related.
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
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton problem
		BufferedReader in = null;
		Vector<Entity> c = null;
		
		try {

			String entitystr = java.net.URLEncoder.encode(entity, "ISO-8859-1");
			String url = Config.getStringValue(Config.SWARM_RAILS_URL) + "/" + entitystr + ".json";

			Debug.Log(Debug.TRACE, "url=" + url);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			request.setURI(new URI(url));

			// set the user agent to from the phone os information
			String useragent = "swarm"
					+ Version.getVersion() + " " + Version.getBuildNum();
			request.setHeader("User-Agent", useragent);
			Debug.Log(Debug.TRACE, "useragent=" + useragent);

			// execute the http GET
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
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
				for (int i=0; i<res.size(); i++) {
					JSONObject o = (JSONObject)res.get(i);
					Project p = new Project();
					p.Name = o.getString("name");
					p.Repo = o.getString("repo");
					String[] tokens1 = p.Repo.split("/");
					String[] tokens2 = tokens1[1].split("\\.");
					Debug.Log(Debug.TRACE, "parsed out base name "+tokens2[0]);
					p.BaseName = tokens2[0];
					p.BuilderType = Builder.BUILDER_TYPE_XCODE;
					Debug.Log(Debug.TRACE, "adding " + p.Name + " " + p.Repo);
					c.add(p);
				}
			}
			
		} catch (Exception e) {
			
			//
			// Note that this exception may be json parsing related or HTTP related.
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
}

