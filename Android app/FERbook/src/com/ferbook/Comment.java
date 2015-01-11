package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Comment extends AsyncTask<Object, Void, Void> {

	private String commentId=null;
	private String error_info=null;
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"post/comment/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	String userId=(String) arg0[0];
    	String postId=(String) arg0[1];
    	String message=(String) arg0[2];
    	
    	sucelje = (prenesi) arg0[3];
    	
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair us=new BasicNameValuePair("userId", userId);
    	NameValuePair pos=new BasicNameValuePair("postId", postId);
    	NameValuePair mess=new BasicNameValuePair("message", message);
    	
    	params.add(us);
    	params.add(pos);
    	params.add(mess);
            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONArray data = jsonObj.getJSONArray("data");
    			
    			commentId = data.getString("postId");
    			
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
        sucelje.prenesi_comment(commentId, error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_comment(String commentId, String error); //action je "like" ili "unlike"
    }



}
