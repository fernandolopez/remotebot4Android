/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.remotebot;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.*;
import ar.edu.unlp.linti.robot.exceptions.*;

public class AndroidBridge {
	public static synchronized JSONObject post(String url, JSONArray commands) throws CommunicationException{
		HttpResponse response = null;
		String str = null;
		JSONObject jsonResponse = null;
		url = url.trim();

		// Formulario
		ArrayList<NameValuePair> form = new ArrayList<NameValuePair>();
		form.add(new BasicNameValuePair("commands", commands.toString()));
		
		// Cliente http sin tanto buffering
		BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpClient client = new DefaultHttpClient(httpParams);		
		
        HttpPost post = new HttpPost(url);
        try {
			post.setEntity(new UrlEncodedFormEntity(form, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new ClientSideException(e.toString());
		}
        
        try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			throw new ClientSideException(e.toString());
		} catch (IOException e) {
			throw new ConnectionException(e.toString());
		}
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() != HttpURLConnection.HTTP_OK){
        	throw new CommunicationException();
        }

        try {
			str = new String(EntityUtils.toByteArray(response.getEntity()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new CommunicationException(e.toString());
		} catch (IOException e) {
			throw new CommunicationException(e.toString());
		}
 
		try {
			jsonResponse = new JSONObject(str);
			if (jsonResponse.getString("type").equals("exception")){
				// Si contestó una excepción codificada en JSON
				// lanzamos una excepción Java
				throw new ServerSideException(jsonResponse.getString("name"));
			}
		} catch (JSONException e) {
			throw new ServerSideException(e.toString());
		}
		return jsonResponse;

	}
	public static JSONObject moduleCommand(String url, String command, JSONArray args) throws CommunicationException{ 
		JSONArray container = new JSONArray();
		JSONObject message = new JSONObject();
		JSONObject jsonResponse = null;
		container.put(message);
		if (args == null) args = new JSONArray();	
		try {
			message.put("target", "module");
			message.put("command", command);
			message.put("args", args);
		} catch (JSONException e) {
			throw new ClientSideException(e.getMessage());
		}		
		// Cliente en un thread separado
		jsonResponse = post(url, container);
		return jsonResponse;
	}

}
