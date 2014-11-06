package com.ferbook;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





public class login extends AsyncTask<String, Void, String> {
	
	private String error_info=null;
	private prenesi sucelje;
	private static String url = "http://vdl.hr/user/login";
	
	
    protected String doInBackground(String... arg0) {
    	String username=arg0[0];
    	String pass = arg0[1];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("username", username);
    	NameValuePair pas=new BasicNameValuePair("password", pass);
    	
    	params.add(user); params.add(pas);
    	
    	
    	String id=null;
    	ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj= new JSONObject(jsonStr);
    			JSONArray data = jsonObj.getJSONArray("data");
    			JSONObject id_data = data.getJSONObject(0);
    			id= id_data.getString("userId");
    			
    			spremi_id(id);
    		}
    		catch(JSONException e){
    			e.printStackTrace();
    			//error=
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
			PrintWriter out
			   = new PrintWriter(new BufferedWriter(new FileWriter("id.txt")));
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
