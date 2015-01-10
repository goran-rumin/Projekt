package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;

public class addGallery extends AsyncTask<Object, Void, String> {
	private prenesi sucelje;
	private static String url="http://vdl.hr/ferbook/user/addGallery/index.php";
	private String albumId=null, error_info=null;
	
	

	@Override
	protected String doInBackground(Object... arg0) {
		String userId = (String) arg0[0];
		String name = (String ) arg0[1];
		sucelje = (prenesi) arg0[1];
		
		
		NameValuePair user=new BasicNameValuePair("userId", userId);
		NameValuePair na=new BasicNameValuePair("name", name);
		
		
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(user);
		params.add(na);
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			
    			albumId=data.getString("albumId");
    			
    		
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
	        sucelje.prenesi_image(albumId ,error_info);	
	    }
	
	public interface prenesi{
    	void prenesi_image(String albumId, String error);	//then set image to imageview using code in your activity.
    }
	


}
