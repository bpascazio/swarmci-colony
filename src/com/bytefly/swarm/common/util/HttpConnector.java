package com.bytefly.swarm.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bytefly.swarm.colony.util.Version;
import com.bytefly.swarm.common.util.Config;
import com.bytefly.swarm.common.util.Debug;

// 
// This class provides a simple stable HTTP communication to get weather information.
//

public class HttpConnector {

	public final static int ERROR_CODE_COMMUNICATON_PROBLEM = -1;
	public final static int ERROR_CODE_SUCCESS 			  	= 0;
	public final static int ERROR_CODE_CITY_NOT_FOUND	    = 1;
	
	public String tempstr = "";
	public String infostr = "";
	public int error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton problem
	
	public boolean getEntityList(String entity) {

		boolean updated = false; // assume failure
		error_code = ERROR_CODE_COMMUNICATON_PROBLEM; // assume communicaton problem
		String ServerURL = "";
		BufferedReader in = null;

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
/*			
			JSONTokener tokener = new JSONTokener(page);
			JSONObject res = (JSONObject) tokener.nextValue();
			int resultjson = (int) res.getLong("result");
			error_code = resultjson;
			if (resultjson == 0) {
				tempstr = res.getString("temp");
				infostr = res.getString("info");
				updated = true;
				Log.d(TAG, "operation success");
			} else {
				// error response code
				Log.d(TAG, "error response code=" + resultjson);
			}
			*/
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
		
		return updated;
	}
}

