package com.ferbook;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class Search extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	private String userId=null, name=null,  lastname=null, profilePicture=null, username=null;
	
	int br_ljudi=0;
	List<String> userIds = new ArrayList<String>();
	List<String> names=new ArrayList<String>();
	List<String> lastnames= new ArrayList<String>();
	List<Drawable> profilePictures=new ArrayList<Drawable>();
	List<String> usernames= new ArrayList<String>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/messages/conversation/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	String query=(String) arg0[0];
    	sucelje = (prenesi) arg0[2];
    	
    	Activity ak=(MainActivity) arg0[2];
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user1=new BasicNameValuePair("userId", Vrati_id.vrati(ak));
    	NameValuePair user2=new BasicNameValuePair("query", query);
    	
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
    				error_info="No people";
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    				
    			}
    			catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    		}
    			
    			
    		JSONObject covjek=new JSONObject();
    			br_ljudi=0;
    			
    			while(true){
    				try {
						covjek = data.getJSONObject(br_ljudi);
    			 
						userId = covjek.getString("userId");
						name = covjek.getString("name");
						lastname = covjek.getString("lastname");
						profilePicture = covjek.getString("profilePicture");
						username= covjek.getString("username");
						//ako sve ovo gore uspije i ako nejde u catch:
						
						userIds.add(userId);
						names.add(name);
						lastnames.add(lastname);
						profilePictures.add(vrati_sliku(profilePicture));
						usernames.add(username);
						
						
    					br_ljudi++;
    					
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
        sucelje.prenesi_search(userIds, names, lastnames, profilePictures, usernames, br_ljudi,  error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_search(List<String> userIds, List<String> names, List<String> lastnames, List<Drawable> profilePictures
    			, List<String> usernames ,int broj_ljudi, String error);
    }
    
    
    private static Drawable vrati_sliku(String url) {
	    try {
	        InputStream is = (InputStream) new URL(url).getContent();
	        Drawable d = Drawable.createFromStream(is, "src name");
	        return d;
	    } catch (Exception e) {
	        return null;
	    }
	}
    
   


}
//{ "data" : [{"userId":id, "name":name, 
//"lastName":lastname, "profilePicture":url, "username":username}, 
//{second user}, {third user}, …,{nth user}] , "error" : [] }