package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Info extends AsyncTask<String, Void, Void> {
	private prenesi_info sucelje;
	private static String url="http://vdl.hr/ferbook/user/info";
	private String userId=null, username=null , name=null, lastName=null, email=null, error_info=null;
	

	@Override
	protected Void doInBackground(String... arg0) {
		NameValuePair user=new BasicNameValuePair("userId", arg0[0]);
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(user);
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONArray data = jsonObj.getJSONArray("data");
    			
    			userId = data.getString("userId");
    			username=data.getString("username");
    			name=data.getString("name");
    			lastName=data.getString("lastName");
    			email=data.getString("email");
    		
    		
    		}
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa userid ne postoji:
    			e.printStackTrace();
    			try{
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    				
    			}
    			catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			
    		}
    	}
    	else error_info="DB does not respond";
    	
		
		return null;
	}
	
	protected void onPostExecute() {
	        sucelje.prenesi(userId,username,name,lastName,email,error_info);	//{ "data" : ["userId" : id,  "username":username, "name" : name, "lastName" : lastname, "email" : email] , "error" : [] }
	    }
	
	public interface prenesi_info{
    	void prenesi(String userId, String username,String name, String lastName, String email,String error);
    }

}
