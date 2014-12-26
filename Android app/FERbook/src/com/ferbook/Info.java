package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Info extends AsyncTask<Object, Void, Void> {
	private prenesi sucelje;
	private static String url="http://vdl.hr/ferbook/user/info/index.php";
	private String userId=null, username=null , name=null, lastName=null, email=null, error_info=null;
	

	@Override
	protected Void doInBackground(Object... arg0) {
		NameValuePair user=new BasicNameValuePair("userId", (String) arg0[0]);
		sucelje = (prenesi) arg0[1];
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(user);
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONArray data = jsonObj.getJSONArray("data");
    			
    			//userId = data.getString("userId");
    			username=data.getString("username");
    			name=data.getString("name");
    			lastName=data.getString("lastname");
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
	
	protected void onPostExecute(Void params) {
	        sucelje.prenesi_info(userId,username,name,lastName,email,error_info);	//{ "data" : ["userId" : id,  "username":username, "name" : name, "lastName" : lastname, "email" : email] , "error" : [] }
	    }
	
	public interface prenesi{
    	void prenesi_info(String userId, String username,String name, String lastName, String email,String error);
    }

}
