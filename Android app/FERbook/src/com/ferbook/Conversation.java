package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Conversation extends AsyncTask<Object, Void, String> {

	private String error_info=null;
	private String message=null, senderId=null,  timestamp=null;
	private int flag=-1;
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/messages/conversation/index.php";
	
	
    protected String doInBackground(Object... arg0) {
    	String userId1=(String) arg0[0];
    	String userId2 = (String) arg0[1];
    	int br_poruke = (Integer) arg0[2]; //redni broj poruke
    	sucelje = (prenesi) arg0[3];
    	//kontekst = (ConversationActivity) arg0[3];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user1=new BasicNameValuePair("userId1", userId1);
    	NameValuePair user2=new BasicNameValuePair("userId2", userId2);
    	
    	params.add(user1);
    	params.add(user2);
            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONArray data = jsonObj.getJSONArray("data");
    			JSONObject poruka = data.getJSONObject(br_poruke);
    			 
    			message = poruka.getString("message");
    			senderId = poruka.getString("senderid");
    			timestamp = poruka.getString("timestamp");
    			flag = poruka.getInt("flag");
    			
    		}
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
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

    protected void onPostExecute() {
        sucelje.prenesi_conversation(message, senderId, timestamp, flag,  error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_conversation(String message, String SenderId, String timestamp, int flag, String error);
    }



}
