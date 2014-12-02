package com.ferbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class Newsfeed extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	
	//int br_komentara=0;
	
	private String postId=null, text=null ,url_u_postu=null , timestamp=null ,senderId=null, senderName=null, senderLastname=null, 
			senderPicture=null, senderUsername=null, senderEmail=null,  recipientId=null, recipientName=null ,recipientLastname=null, 
			recipientPicture=null, recipientUsername=null, recipientEmail=null;
	int comments=0, likes=0;
	
	int br_postova;
	
		
	List<String> postIds = new ArrayList<String>();
	List<String> texts = new ArrayList<String>();
	List<String> urlovi_u_postu = new ArrayList<String>();
	List<String> timestamps = new ArrayList<String>();
	List<String> senderIds=new ArrayList<String>();
	List<String> senderNames= new ArrayList<String>();
	List<String> senderLastnames=new ArrayList<String>();
	List<Drawable> senderPictures=new ArrayList<Drawable>();
	List<String> senderUsernames=new ArrayList<String>();
	List<String> senderEmails=new ArrayList<String>();
	List<String>recipientIds=new ArrayList<String>();
	List<String>recipientNames=new ArrayList<String>();
	List<String>recipientLastnames=new ArrayList<String>();
	List<Drawable>recipientPictures=new ArrayList<Drawable>();
	List<String>recipientUsernames=new ArrayList<String>();
	List<String>recipientEmails=new ArrayList<String>();
	List<Integer>broj_komentara=new ArrayList<Integer>();
	List<Integer>broj_likeova=new ArrayList<Integer>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/post/newsfeed/index.php";
	
	
    protected Void doInBackground(Object... arg0) { //ne trebam ništa primati! id imam iz vrati_id, a timestamp iz prošlog timestampa
    	sucelje = (prenesi) arg0[0];
    	
    	
    	
    	Activity ak=(MainActivity) arg0[0];			//MainActivity?
    	    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("userId", Vrati_id.vrati(ak));    
    	NameValuePair time=new BasicNameValuePair("timestamp", vrati_vrijeme(ak) );
    	params.add(user);     
    	params.add(time);
    	
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
    				error_info="No posts";		//ako je data s razlogom prazan
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    			}catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			return null;
    		}
    			
    			
    		JSONObject post=new JSONObject();
    		
    		br_postova=0;
    			
    			while(true){
    				try {
						post = data.getJSONObject(br_postova);
						
						postId=post.getString("postId");
						text=post.getString("text");
						url_u_postu=post.getString("url");
						timestamp=post.getString("timestamp");
						senderId=post.getString("senderId");
						senderName=post.getString("senderName");
						senderLastname=post.getString("senderLastname");
						senderPicture=post.getString("senderPicture");
						senderUsername=post.getString("senderUsername");
						senderEmail=post.getString("senderEmail");
						recipientId=post.getString("recipientId");
						recipientName=post.getString("recipientName");
						recipientLastname=post.getString("recipientLastname");
						recipientPicture=post.getString("recipientPicture");
						recipientUsername=post.getString("recipientUsername");
						recipientEmail=post.getString("recipientEmail");
						comments=post.getInt("comments");
						likes=post.getInt("likes");
						
						if(br_postova==0) spremi_vrijeme(timestamp,ak);		//spremam vrijeme najnovijeg posta kojeg sam dobio
						
						postIds.add(postId);	
						texts.add(text);		
						urlovi_u_postu.add(url_u_postu);	
						timestamps.add(timestamp);
						senderIds.add(senderId);
						senderNames.add(senderName);
						senderLastnames.add(senderLastname);
						senderPictures.add(vrati_sliku(senderPicture));
						senderUsernames.add(senderUsername);
						senderEmails.add(senderEmail);
						recipientIds.add(recipientId);
						recipientNames.add(recipientName);
						recipientLastnames.add(recipientLastname);
						recipientPictures.add(vrati_sliku(recipientPicture));
						recipientUsernames.add(recipientUsername);
						recipientEmails.add(recipientEmail);
						broj_komentara.add(comments);
						broj_likeova.add(likes);
						
						
						
						
    					br_postova++;
    					
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
        sucelje.prenesi_newsfeed(postIds,texts,urlovi_u_postu,timestamps,senderIds,senderNames,senderLastnames,senderPictures,senderUsernames, senderEmails,recipientIds,recipientNames,recipientLastnames,recipientPictures, recipientUsernames,recipientEmails,broj_komentara, broj_likeova,br_postova,error_info);
    }
    
    
       
    
    public interface prenesi{
    	void prenesi_newsfeed(List<String> postIds,List<String> texts,List<String> urlovi_u_postu,List<String> timestamps,List<String> senderIds,List<String> senderNames,List<String> senderLastnames,List<Drawable> senderPictures,List<String> senderUsernames, List<String> senderEmails,List<String> recipientIds,List<String> recipientNames,List<String> recipientLastnames,List<Drawable> recipientPictures,List<String>  recipientUsernames,List<String> recipientEmails,List<Integer> broj_komentara, List<Integer> broj_likeova, int br_postova,String error_info);
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
    
private static String vrati_vrijeme(Activity ak){
		
		String vrijeme="1990-01-01 10:44:57";		//neka davna godina ako do sad nisam osvjezavao newsfeed
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(ak.openFileInput("timestamp.txt")));
			vrijeme= bf.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vrijeme;
	}

private void spremi_vrijeme(String vrijeme, Activity kontekst){
	try {
		OutputStreamWriter out = new OutputStreamWriter(kontekst.openFileOutput("timestamp.txt", Context.MODE_PRIVATE));
		out.write(vrijeme);			//ovo ce prepisati staru vrijednost
		out.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
}
    


}