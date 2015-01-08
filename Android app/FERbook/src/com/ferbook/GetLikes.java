package com.ferbook;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class GetLikes extends AsyncTask<Object, Void, Void> {//{ "data" : [{"likeId":id, "timestamp" : timestamp,"userId":id, "name":name, "lastName":lastname, "picture":url, "username":username, "email": email}, {second comment}, …{nth comment}] , "error" : [] }

	private String error_info=null;
	private String likeId=null, timestamp=null, userId=null, name=null, lastName=null,  picture=null, username=null, email=null;
	
	private int br_likeova=0;
	
		
	//List<String> likeIds = new ArrayList<String>();
	List<String> timestamps = new ArrayList<String>();
	List<String> userIds = new ArrayList<String>();
	List<String> names = new ArrayList<String>();
	List<String> lastNames=new ArrayList<String>();
	List<Drawable> pictures = new ArrayList<Drawable>();
	List<String> usernames=new ArrayList<String>();
	List<String> emails=new ArrayList<String>();
	
	static Activity ak;
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/post/getLikes/index.php";
	
	
    protected Void doInBackground(Object... arg0) {    //(postId, sucelje )
    	//String postId=(String) arg0[0];

    	sucelje = (prenesi) arg0[1];
    	ak= (MainActivity) arg0[2];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("postId", (String) arg0[0]);    	
    	params.add(user);            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	Log.e("GETLjson", jsonStr);
    	
    	JSONObject data=new JSONObject();
    	JSONArray imena=new JSONArray();
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			
    			data = jsonObj.getJSONObject("data");
    			
    			imena=data.names(); //JSONArray
    	
    			int i;
    			List<String> jsonValues = new ArrayList<String>();
    			
    			for (i = 0; i < imena.length(); i++)		//likeovi od starijih prema novijima, od manjih prema vecim idjevima
    			   jsonValues.add(imena.getString(i));
    			
    			Collections.sort(jsonValues);				//ovo je ascending, tj od manjih prema vecima
    			//Collections.reverse(jsonValues);
    			
    			JSONArray sortedJsonArray = new JSONArray();
    			
    			for(i=0;i<jsonValues.size();i++)
    				sortedJsonArray.put(jsonValues.get(i));
    				
    			imena=sortedJsonArray;				//konacno
    			
    			
    			
    			//Log.e("JSONIMENA", imena.toString());
    			
    			data.get(imena.getString(0)); //ako je data prazan ide se na catch i parsiranje errora
    			
    			
    		}catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
    			e.printStackTrace();
    			try{
    				error_info="No likes";		//ako je data s razlogom prazan
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    			}catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			return null;
    		}
    			
    			
    		JSONObject like=new JSONObject();
    		
    		br_likeova=0;
    			
    			while(true){
    				try {
						like = data.getJSONObject(imena.getString(br_likeova));
						
						//likeId=like.getString("likeId");
						timestamp=like.getString("timestamp");
						userId=like.getString("userId");
						name=like.getString("name");
						lastName=like.getString("lastname");
						picture=like.getString("picture");
						username=like.getString("username");
						email=like.getString("email");
						
						//ako sve ovo gore uspije i ako nejde u catch:
						
						//likeIds.add(likeId);
						timestamps.add(timestamp);
						userIds.add(userId);
						names.add(name);
						lastNames.add(lastName);
						pictures.add(vrati_sliku(picture));
						usernames.add(username);
						emails.add(email);
											
									
						
    					br_likeova++;
    					
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
        sucelje.prenesi_getlikes(timestamps, userIds, names, lastNames, pictures, usernames, emails, br_likeova,error_info);
    }
    
    
       
    
    public interface prenesi{
    	void prenesi_getlikes(List<String> timestamps, List<String> userIds, List<String> names, List<String> lastNames, List<Drawable> pictures, List<String> usernames, List<String> emails, int broj_likeova, String error);
    }

    
    private static Drawable vrati_sliku(String url) {
    	Log.e("URLprijeParsinga", url);
    	if(url==null){
    		Drawable d = ak.getResources().getDrawable( R.drawable.ferbook );
	    	return d;
    	}	
    	
	    try {
	    	url = url.substring(0, url.length()-4);
    		url= url + "thm.jpg";
	    	
    		Log.e("URLposlijeParsinga", url);
	        InputStream is = (InputStream) new URL(url).getContent();
	        Drawable d = Drawable.createFromStream(is, "src name");
	        return d;
	    } catch (Exception e) {
	    	Drawable d = ak.getResources().getDrawable( R.drawable.ferbook );
	    	return d;
	    }
	}


}