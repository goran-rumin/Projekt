package com.ferbook;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Conversation extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	private String message=null, senderId=null,  timestamp=null;
	private int flag=-1;
	int br_poruka=0;
	List<String> messages = new ArrayList<String>();
	List<String> senderIds=new ArrayList<String>();
	List<String> timestamps= new ArrayList<String>();
	List<Integer> flags=new ArrayList<Integer>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"messages/conversation/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	String userId1=(String) arg0[0];
    	String userId2 = (String) arg0[1];
    	//int br_poruke = (Integer) arg0[2]; //redni broj poruke
    	sucelje = (prenesi) arg0[2];
    	//kontekst = (ConversationActivity) arg0[3];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user1=new BasicNameValuePair("userId1", userId1);
    	NameValuePair user2=new BasicNameValuePair("userId2", userId2);
    	
    	params.add(user1);
    	params.add(user2);
            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	JSONArray data=new JSONArray();
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			data = jsonObj.getJSONArray("data");	//exception
    			data.get(0); //ako je data prazan ide se na catch i parsiranje errora
    			}	
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
    			e.printStackTrace();
    			try{
    				error_info="No messages";
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    				
    			}
    			catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    		}
    			
    			
    		JSONObject poruka=new JSONObject();
    			br_poruka=0;
    			
    			while(true){
    				try {
						poruka = data.getJSONObject(br_poruka);
    			 
						message = poruka.getString("message");
						senderId = poruka.getString("senderId");
						timestamp = poruka.getString("timestamp");
						flag = poruka.getInt("flag");
						//ako sve ovo gore uspije i ako nejde u catch:
						
						messages.add(message);
						senderIds.add(senderId);
						timestamps.add(timestamp);
						flags.add(flag);						
						
    					br_poruka++;
    					
					} catch (JSONException e) {
						e.printStackTrace();
						break;
					}
    				
    			}
    			
    		}
    		
    			
    			
    		
    	
    	else error_info="DB does not respond";
		return null;
    	
    	
    }

    protected void onProgressUpdate(Integer... progress) {
        
    }

    protected void onPostExecute(Void param) {
        sucelje.prenesi_conversation(messages, senderIds, timestamps, flags, br_poruka,  error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_conversation(List<String> messages, List<String> SenderIds, List<String> timestamps, List<Integer> flags, int broj_poruka, String error);
    }
    
   


}