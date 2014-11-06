package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class info extends AsyncTask<String, Void, Void> {
	private prenesi_info sucelje;
	private static String url="http://vdl.hr/user/info";
	private String userId=null, username=null , name=null, lastName=null, email=null, error_info=null;
	

	@Override
	protected Void doInBackground(String... arg0) {
		NameValuePair user=new BasicNameValuePair("userId", arg0[0]);
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(user);
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
		
		return null;
	}
	
	protected void onPostExecute() {
	        sucelje.prenesi(userId,username,name,lastName,email,error_info);	//{ "data" : ["userId" : id,  "username":username, "name" : name, "lastName" : lastname, "email" : email] , "error" : [] }
	    }
	
	public interface prenesi_info{
    	void prenesi(String userId, String username,String name, String lastName, String email,String error);
    }

}
