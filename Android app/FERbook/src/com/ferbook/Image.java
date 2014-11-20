package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Image extends AsyncTask<String, Void, Void> {
	private prenesi sucelje;
	private static String url="http://vdl.hr/ferbook/photos/image/index.php";
	private String url_slike=null, error_info=null;
	

	@Override
	protected Void doInBackground(String... arg0) {
		NameValuePair user=new BasicNameValuePair("postId", arg0[0]);
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(user);
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			
    			url_slike=data.getString("url");
    		
    		}
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa url ne postoji:
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
	        sucelje.prenesi_image(url_slike ,error_info);	
	    }
	
	public interface prenesi{
    	void prenesi_image(String url_slike, String error);
    }

}
