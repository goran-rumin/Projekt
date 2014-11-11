package com.ferbook;

import java.io.BufferedWriter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Login extends AsyncTask<Object, Void, String> {
	
	private String error_info=null;
	private prenesi sucelje;
	Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/user/login/index.php";
	
	
    protected String doInBackground(Object... arg0) {
    	String username=(String) arg0[0];
    	String pass = (String) arg0[1];
    	sucelje = (prenesi) arg0[2];
    	kontekst = (LoginActivity) arg0[2];
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("username", username);
    	NameValuePair pas=new BasicNameValuePair("password", pass);
    	
    	params.add(user);
    	params.add(pas);
        
    	String id=null;
    	
    	ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONObject err = jsonObj.getJSONObject("error");
    			id = data.getString("userId");
    			spremi_id(id);
    		}
    		catch(JSONException e){
    			e.printStackTrace();
    		}
    	}
     return id;
    }

    protected void onProgressUpdate(Integer... progress) {
        
    }

    protected void onPostExecute(String id) {
        sucelje.prenesi_login(id, error_info);
    }
    
    private void spremi_id(String id){
    	try {
    		OutputStreamWriter out = new OutputStreamWriter(kontekst.openFileOutput("id.txt", Context.MODE_PRIVATE));
    		out.write(id);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public interface prenesi{
    	void prenesi_login(String id, String error);
    }



}
