package com.ferbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import android.util.Log;

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
	
	Drawable nova;
	List<String> postIds = new ArrayList<String>();
	List<String> texts = new ArrayList<String>();
	List<Drawable> urlovi_u_postu = new ArrayList<Drawable>();
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
	private static final Map<String, Drawable> slikice= new HashMap<String, Drawable>(); //url, slika
	
	private prenesi sucelje;
	//Activity kontekst;
	private String url;
	static Activity ak;
	
	
    protected Void doInBackground(Object... arg0) { //(int broj, this)   //1 na početku, a dalje se broj povecava, najbolje u activityu
    	String userId=(String) arg0[0];
    	int broj=(Integer) arg0[1];				//1 za prvih 20 postova, 2 za drugih 20...
    	int vrsta = (Integer) arg0[2];		//NEWS=0, WALL=1, imate i public static varijable koje mozete koristiti
    	sucelje = (prenesi) arg0[3];		//samo trebam this, tj, activity
    	  	
    	
    	ak=(MainActivity) arg0[4];			//MainActivity?
    	    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("userId", userId);    
    	NameValuePair time=new BasicNameValuePair("offset", String.valueOf(broj) );	
    	params.add(user);     
    	params.add(time);
    	
    	if(vrsta==NEWS) url = Vrati_id.ROOT+"post/newsfeed/index.php";
    	else if (vrsta==WALL) {
    		url = Vrati_id.ROOT+"post/wall/index.php";
    		NameValuePair user2=new BasicNameValuePair("userId2", Vrati_id.vrati(ak));	
        	params.add(user2);  
    	}
    	else {
    		error_info="Kriva vrsta";
    		return null;
    	}
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	//Log.e("JSONERROR", ""+jsonStr);
    	
    	JSONObject data=new JSONObject();
    	JSONArray imena=new JSONArray();
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			data = jsonObj.getJSONObject("data");
    			
    			imena=data.names(); //JSONArray
    	
    			int i;
    			List<Integer> jsonValues = new ArrayList<Integer>();
    			
    			for (i = 0; i < imena.length(); i++)		//trebam poredati od novijih prema starijima (od vecih prema manjima)
    			   jsonValues.add(Integer.parseInt(imena.getString(i)));
    			
    			Collections.sort(jsonValues);				//ovo je ascending, tj od manjih prema vecima
    			Collections.reverse(jsonValues);
    			
    			JSONArray sortedJsonArray = new JSONArray();
    			
    			for(i=0;i<jsonValues.size();i++)
    				sortedJsonArray.put(Integer.toString(jsonValues.get(i)));
    				
    			imena=sortedJsonArray;				//konacno
    			
    			
    			
    			//Log.e("JSONIMENA", imena.toString());
    			
    			data.get(imena.getString(0)); //ako je data prazan ide se na catch i parsiranje errora
    			
    		}catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
    			e.printStackTrace();
    			try{
    				error_info="No more posts";		//ako je data s razlogom prazan
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
						post = data.getJSONObject(imena.getString(br_postova));
						
						postId=post.getString("postId");
						text=post.getString("text");
						url_u_postu=post.getString("url");
						timestamp=post.getString("timestamp");  //"2014-12-06 23:09:57"
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
							if(broj!=1 && timeConversion(timestamp)>=timeConversion(vrati_vrijeme(ak))){//ako se ne refresha od pocetka
								br_postova++;							//brisi postove koje sam vec poslao u mainactivity
								continue;	
																			
						}}
						
						zadnje_vrijeme=timestamp;		//u zadnje_vrijeme je najstariji post kojeg sam dobio
						
						postIds.add(postId);	
						texts.add(text);		
						
						if(url_u_postu.equals(""))urlovi_u_postu.add(null);
						else urlovi_u_postu.add(Image.vrati_sliku(url_u_postu));	
						
						timestamps.add(timestamp);  
						senderIds.add(senderId);
						senderNames.add(senderName);
						senderLastnames.add(senderLastname);
						
						if(slikice.containsKey(senderPicture))	//senderPicture je url
							senderPictures.add(slikice.get(senderPicture));
						else{
							senderPictures.add(nova=vrati_sliku(senderPicture));
							slikice.put(senderPicture, nova);
						}
						
						senderUsernames.add(senderUsername);
						senderEmails.add(senderEmail);
						recipientIds.add(recipientId);
						recipientNames.add(recipientName);
						recipientLastnames.add(recipientLastname);
						
						if(slikice.containsKey(recipientPicture))	//senderPicture je url
							recipientPictures.add(slikice.get(recipientPicture));
						else{
							recipientPictures.add(nova=vrati_sliku(recipientPicture));
							slikice.put(recipientPicture, nova);
						}
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
    	void prenesi_newsfeed(List<String> postIds,List<String> texts,List<Drawable> urlovi_u_postu,List<String> timestamps,List<String> senderIds,List<String> senderNames,List<String> senderLastnames,List<Drawable> senderPictures,List<String> senderUsernames, List<String> senderEmails,List<String> recipientIds,List<String> recipientNames,List<String> recipientLastnames,List<Drawable> recipientPictures,List<String>  recipientUsernames,List<String> recipientEmails,List<Boolean> liked_lista_boolean, List<Integer> broj_likeova,String error_info);
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

//private String pretvori_u_vrijeme(String unix_vrijeme){	//vraća čitljivo vrijeme iz unix vremena
	//	long dv = Long.valueOf(unix_vrijeme)*1000;// it needs to be in miliseconds //unix vrijeme je broj sekundi od 1970.
		//Date df = new java.util.Date(dv);
	//	String vv = new SimpleDateFormat("dd. M., yyyy HH:mm").format(df);
	//	return vv;
//}





public long timeConversion(String time){
	DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //"2014-12-06 23:09:57"

	long unixtime=-1;
	
    dfm.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));//Specify your timezone 
    try
    {
    	unixtime = dfm.parse(time).getTime();  
    	unixtime=unixtime/1000;
    } 
    catch (ParseException e) 
	{
    e.printStackTrace();
	}
    
    return unixtime;
}


}