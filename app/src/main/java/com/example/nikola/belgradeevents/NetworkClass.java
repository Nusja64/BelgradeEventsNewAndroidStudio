package com.example.nikola.belgradeevents;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.*;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


public class NetworkClass{
	

	public final static String SERVICE_ADDRESS = "http://private-60f2d-eventer2.apiary-mock.com/api/v1/events"; 
	
	public final static String tag = "error";
	private final static String SERVICE_ID = SERVICE_ADDRESS + "/";

	private Context context;
	
	
	
	public NetworkClass(Context context)
	{		
		this.context=context;
		
	}
	

	public JSONArray searchRequest()
			throws Exception {
		

				JSONArray jArray = null;
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				HttpGet request = new HttpGet(SERVICE_ADDRESS);
		    
				request.setHeader("Accept", "application/json");
				request.setHeader("Content-type", "application/json");
		    
				HttpResponse response = httpClient.execute(request);	 
				HttpEntity entity = response.getEntity();
		    	
				 if (response.getStatusLine().getStatusCode() == 200)
                 {  
					String data = EntityUtils.toString(entity,"utf-8");
					
					try {
						
						jArray = new JSONArray(data);
					//	responseString = jArray.getString(metodResult);
						
					} catch (JSONException e) {
						
						Log.e(tag,"Error with JSONArray converting", e);
					}
                 }
				 return jArray;
			}
	
	public JSONArray searchRequestForImages(int id)
			throws Exception {
		

				JSONArray jArray = null;
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				HttpGet request = new HttpGet(SERVICE_ADDRESS+"/"+id);
		    
				request.setHeader("Accept", "application/json");
				request.setHeader("Content-type", "application/json");
		    
				HttpResponse response = httpClient.execute(request);	 
				HttpEntity entity = response.getEntity();
		    	
				 if (response.getStatusLine().getStatusCode() == 200)
                 {  
					String data = EntityUtils.toString(entity,"utf-8");
					
					try {
						
						jArray = new JSONArray(data);
					//	responseString = jArray.getString(metodResult);
						
					} catch (JSONException e) {
						
						Log.e(tag,"Error with JSONArray converting", e);
					}
                 }
				 return jArray;
			}
	
	
	
}
