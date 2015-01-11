package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Publish extends AsyncTask<Object, Void, Void> {

	private String id=null;
	private String error_info=null;
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"post/publish/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	String sender=(String) arg0[0];
    	String recipient=(String) arg0[1];
    	String message=(String) arg0[2];
    	String url_u_poruci=(String) arg0[3];
    	
    	sucelje = (prenesi) arg0[4];
    	//kontekst = (SendActivity) arg0[3];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair send=new BasicNameValuePair("sender", sender);
    	NameValuePair rec=new BasicNameValuePair("recipient", recipient);
    	NameValuePair mess =new BasicNameValuePair("message",message);
    	NameValuePair ur =new BasicNameValuePair("url",url_u_poruci);
    	
    	params.add(send);
    	params.add(rec);
    	params.add(mess);
    	params.add(ur);
            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONArray data = jsonObj.getJSONArray("data");
    			
    			id = data.getString("postId");
    			
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
        sucelje.prenesi_publish(id, error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_publish(String postId, String error); 
    }



}
