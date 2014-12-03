package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Like extends AsyncTask<Object, Void, Void> {

	private String action=null;
	private String error_info=null;
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/post/like/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	String userId=(String) arg0[0];
    	String postId=(String) arg0[1];
    	
    	sucelje = (prenesi) arg0[2];
    	
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair us=new BasicNameValuePair("userId", userId);
    	NameValuePair pos=new BasicNameValuePair("postId", postId);
    	
    	params.add(us);
    	params.add(pos);
            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONArray data = jsonObj.getJSONArray("data");
    			
    			action = data.getString("action");
    			
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

    protected void onProgressUpdate(Integer... progress) {
        
    }

    protected void onPostExecute(Void param) {
        sucelje.prenesi_like(action, error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_like(String action, String error); //action je "like" ili "unlike"
    }



}
