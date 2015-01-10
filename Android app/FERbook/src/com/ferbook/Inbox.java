package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Inbox extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	private String userId=null, name=null, lastname=null, message=null, senderId=null,  timestamp=null;
	private int flag=-1;
	int br_ljudi=0;
	
	List<String> userIds = new ArrayList<String>();
	List<String> names = new ArrayList<String>();
	List<String> lastnames = new ArrayList<String>();
	List<String> messages = new ArrayList<String>();
	List<String> senderIds=new ArrayList<String>();
	List<String> timestamps= new ArrayList<String>();
	List<Integer> flags=new ArrayList<Integer>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/messages/inbox/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	//String userId=(String) arg0[0];

    	sucelje = (prenesi) arg0[1];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("userId", (String) arg0[0]);    	
    	params.add(user);            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	JSONArray data=new JSONArray();
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			data = jsonObj.getJSONArray("data");
    			data.get(0); //ako je data prazan ide se na catch i parsiranje errora
    		}catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
    			e.printStackTrace();
    			try{
    				error_info="No messages";		//ako je data s razlogom prazan
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    			}catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			return null;
    		}
    			
    			
    		JSONObject poruka=new JSONObject();
    		JSONObject conversation= new JSONObject();
    			br_ljudi=0;
    			
    			while(true){
    				try {
						poruka = data.getJSONObject(br_ljudi);
						
						userId=poruka.getString("userId");
						name=poruka.getString("name");
						lastname=poruka.getString("lastname");
						
						conversation = poruka.getJSONObject("lastMessage");
						
						message=conversation.getString("message");
						senderId=conversation.getString("senderId");
						timestamp=conversation.getString("timestamp");
						flag=conversation.getInt("flag");
						
						//ako sve ovo gore uspije i ako nejde u catch:
						
						userIds.add(userId);
						names.add(name);
						lastnames.add(lastname);
						
						messages.add(message);
						senderIds.add(senderId);
						timestamps.add(timestamp);
						flags.add(flag);						
						
    					br_ljudi++;
    					
					} catch (JSONException e) {
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
        sucelje.prenesi_inbox(userIds, names, lastnames, messages, senderIds, timestamps, flags, br_ljudi,  error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_inbox(List<String> userIds, List<String> names, List<String> lastnames, List<String> messages, List<String> SenderIds, List<String> timestamps, List<Integer> flags, int broj_ljudi, String error);
    }
    


}//kako ovo izvesti? :/
//{ "data" : [{"userId": id, "name":name, "lastname":lastname, "lastMessage" : { "message":message, "senderId": sender, "timestamp":timestamp, "flag":flag}}, {second conversation}, {third conversation}, …, {nth conversation}] , "error" : [] }
