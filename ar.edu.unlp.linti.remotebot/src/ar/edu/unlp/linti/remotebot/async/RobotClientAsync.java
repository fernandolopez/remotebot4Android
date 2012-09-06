/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.remotebot.async;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import ar.edu.unlp.linti.remotebot.Util;

public class RobotClientAsync  extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data

    /**
     * constructor
     * @return 
     */
    public RobotClientAsync(HashMap<String, String> data) {
        mData = data;
    }

    /**
     * background
     */
    @Override
    protected String doInBackground(String... params) {
    	android.os.Debug.waitForDebugger();
    	String eName = null;
    	String eStack = null;
        byte[] result = null;
        String str = "";
        
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setTcpNoDelay(httpParams, true);

        HttpClient client = new DefaultHttpClient(httpParams);
        
        HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
        try {
            // set up post data
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            Iterator<String> it = mData.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            }

            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {    	
            e.printStackTrace();
            eName = e.toString();
            eStack = Util.exceptionToString(e);
        } catch (ClientProtocolException e) {
			e.printStackTrace();
            eName = e.toString();
            eStack = Util.exceptionToString(e);
		} catch (SocketException e) {
			e.printStackTrace();
            eName = e.toString();
            eStack = Util.exceptionToString(e);
		} catch (IOException e) {
			e.printStackTrace();
            eName = e.toString();
            eStack = Util.exceptionToString(e);
		}
        if (eName != null){
        	JSONObject exception = new JSONObject();
        	try {
				exception.put("type", "exception");
				exception.put("name", eName);
        		exception.put("stacktrace", eStack);
        		str = exception.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
        }
        return str;
    }

    /**
     * on getting result
     */
    @Override
    protected void onPostExecute(String result) {
        // something...
    	
    }
}
