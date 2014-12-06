package com.ferbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

@SuppressLint("SimpleDateFormat")
public class Newsfeed extends AsyncTask<Object, Void, Void> {

	public static int NEWS=0, WALL=1; 
	
	private String error_info=null;
	
	//int br_komentara=0;
	
	private String postId=null, text=null ,url_u_postu=null , timestamp=null ,senderId=null, senderName=null, senderLastname=null, 
			senderPicture=null, senderUsername=null, senderEmail=null,  recipientId=null, recipientName=null ,recipientLastname=null, 
			recipientPicture=null, recipientUsername=null, recipientEmail=null;
	boolean liked;
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
	List<Boolean>liked_boolean=new ArrayList<Boolean>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/post/newsfeed/index.php";
	
	
    protected Void doInBackground(Object... arg0) { //(int broj, this)   //1 na početku, a dalje se broj povecava, najbolje u activityu
    	int broj=(Integer) arg0[0];				//1 za prvih 20 postova, 2 za drugih 20...
    	int vrsta = (Integer) arg0[1];		//NEWS=0, WALL=1, imate i public static varijable koje mozete koristiti
    	sucelje = (prenesi) arg0[2];		//samo trebam this, tj, activity
    	
    	
    	
    	Activity ak=(MainActivity) arg0[3];			//MainActivity?
    	    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("userId", Vrati_id.vrati(ak));    
    	NameValuePair time=new BasicNameValuePair("offset", String.valueOf(broj) );	//TODO izmjeniti "broj" kako ce iti u bazi
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
    		
    		String zadnje_vrijeme=null;
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
						liked=post.getBoolean("liked");
						likes=post.getInt("likesNumber");
						
						
						
						if(vrati_vrijeme(ak)!=null){
							if(broj!=1 && Long.valueOf(timestamp)>Long.valueOf(vrati_vrijeme(ak))){//ako se ne refresha od pocetka
								br_postova++;							//brisi postove koje sam vec poslao u mainactivity
								continue;	
																			
						}}
						
						zadnje_vrijeme=timestamp;		//u zadnje_vrijeme je najstariji post kojeg sam dobio
						
						postIds.add(postId);	
						texts.add(text);		
						urlovi_u_postu.add(url_u_postu);	
						timestamps.add(pretvori_u_vrijeme(timestamp));    //pretvaram iz unix_vremena u čitljivo vrijeme
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
						liked_boolean.add(liked);
						//broj_komentara.add(comments);
						broj_likeova.add(likes);
						
						
						
						
    					br_postova++;
    					
					} catch (JSONException e) {
						if(zadnje_vrijeme!=null) spremi_vrijeme(zadnje_vrijeme,ak);
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
        sucelje.prenesi_newsfeed(postIds,texts,urlovi_u_postu,timestamps,senderIds,senderNames,senderLastnames,senderPictures,senderUsernames, senderEmails,recipientIds,recipientNames,recipientLastnames,recipientPictures, recipientUsernames,recipientEmails, liked_boolean, broj_likeova,error_info);
    }
    
    
       
    
    public interface prenesi{
    	void prenesi_newsfeed(List<String> postIds,List<String> texts,List<String> urlovi_u_postu,List<String> timestamps,List<String> senderIds,List<String> senderNames,List<String> senderLastnames,List<Drawable> senderPictures,List<String> senderUsernames, List<String> senderEmails,List<String> recipientIds,List<String> recipientNames,List<String> recipientLastnames,List<Drawable> recipientPictures,List<String>  recipientUsernames,List<String> recipientEmails,List<Boolean> liked_lista_boolean, List<Integer> broj_likeova,String error_info);
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
		
		String vrijeme=null;
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

private String pretvori_u_vrijeme(String unix_vrijeme){	//vraća čitljivo vrijeme iz unix vremena
		long dv = Long.valueOf(unix_vrijeme)*1000;// it needs to be in miliseconds //unix vrijeme je broj sekundi od 1970.
		Date df = new java.util.Date(dv);
		String vv = new SimpleDateFormat("dd. M., yyyy HH:mm").format(df);
		return vv;
}


}