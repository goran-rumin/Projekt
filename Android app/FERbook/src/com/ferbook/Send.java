package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Send extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"messages/send/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	String userId1=(String) arg0[0];
    	String userId2 = (String) arg0[1];
    	String message = (String) arg0[2]; //redni broj poruke
    	sucelje = (prenesi) arg0[3];
    	//kontekst = (SendActivity) arg0[3];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user1=new BasicNameValuePair("userId1", userId1);
    	NameValuePair user2=new BasicNameValuePair("userId2", userId2);
    	NameValuePair mess =new BasicNameValuePair("message",message);
    	
    	params.add(user1);
    	params.add(user2);
    	params.add(mess);
            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	
    	
    	if(jsonStr != null){
    		try{
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    				
    			}
    			catch(JSONException e1){
    				e1.printStackTrace();
    				    			
    		}
    	}
    	else error_info="DB does not respond";
    	
    	
     return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        
    }

    protected void onPostExecute(Void param) {
        sucelje.prenesi_send(error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_send(String error);  //ako nema errora, sve kul
    }



}
