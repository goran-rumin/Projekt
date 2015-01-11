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

public class GetComments extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	private String postId=null, message=null, url_u_poruci=null, timestamp=null, userId=null,  name=null, lastname=null, picture=null, username=null;
	
	int br_komentara=0;
	
		
	List<String> postIds = new ArrayList<String>();
	List<String> messages = new ArrayList<String>();
	List<String> urlovi_u_poruci = new ArrayList<String>();
	List<String> timestamps = new ArrayList<String>();
	List<String> userIds=new ArrayList<String>();
	List<String> names= new ArrayList<String>();
	List<String> lastnames=new ArrayList<String>();
	List<Drawable> pictures=new ArrayList<Drawable>();
	List<String> usernames=new ArrayList<String>();
	List<Boolean> liked = new ArrayList<Boolean>();
	List<String> likesnum = new ArrayList<String>();
	
	static Activity ak;
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"post/getComments/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	//String postId=(String) arg0[0];

    	sucelje = (prenesi) arg0[2];
    	ak= (MainActivity) arg0[3];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair post=new BasicNameValuePair("postId", (String) arg0[0]);  
    	NameValuePair user=new BasicNameValuePair("userId", (String) arg0[1]); 
    	params.add(post);  
    	params.add(user);            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	Log.e("GETCjson", ""+jsonStr);
    	
    	JSONObject data=new JSONObject();
    	JSONArray imena=new JSONArray();
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			data = jsonObj.getJSONObject("data");
    			
    			imena=data.names(); //JSONArray
    	    	
    			int i;
    			List<Integer> jsonValues = new ArrayList<Integer>();
    			
    			for (i = 0; i < imena.length(); i++)		//trebam od starijih prema novijima, to ima logike za komentare, znaci od manjih prema vecim idjevima
    			   jsonValues.add(Integer.parseInt(imena.getString(i)));
    			
    			Collections.sort(jsonValues);				//ovo je ascending, tj od manjih prema vecima
    			//Collections.reverse(jsonValues);
    			
    			JSONArray sortedJsonArray = new JSONArray();
    			
    			for(i=0;i<jsonValues.size();i++)
    				sortedJsonArray.put(Integer.toString(jsonValues.get(i)));
    				
    			imena=sortedJsonArray;				//konacno
    			
    			
    			
    			Log.e("JSONIMENAcomments", imena.toString());
    			
    			data.get(imena.getString(0)); //ako je data prazan ide se na catch i parsiranje errora
    			
    			
    			
    		}catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
    			e.printStackTrace();
    			try{
    				error_info="No comments";		//ako je data s razlogom prazan
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    			}catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			return null;
    		}
    			
    			
    		JSONObject komentar=new JSONObject();
    		
    		br_komentara=0;
    			
    			while(true){
    				try {
						komentar = data.getJSONObject(imena.getString(br_komentara));
						
						postId=komentar.getString("id");
						message=komentar.getString("message");
						url_u_poruci=komentar.getString("url");
						timestamp=komentar.getString("timestamp");
						userId=komentar.getString("userId");
						name=komentar.getString("name");
						lastname=komentar.getString("lastname");
						picture=komentar.getString("picture");		//picture url
						username=komentar.getString("username");
						String like = komentar.getString("liked");
						String likenum = komentar.getString("likesNumber");
						//ako sve ovo gore uspije i ako nejde u catch:
						
						postIds.add(postId);
						messages.add(message);
						urlovi_u_poruci.add(url_u_poruci);
						timestamps.add(timestamp);
						userIds.add(userId);
						names.add(name);
						lastnames.add(lastname);
						pictures.add(vrati_sliku(picture));
						usernames.add(username);
						if(like.equals("true"))
							liked.add(true);
						else
							liked.add(false);
						likesnum.add(likenum);
						
    					br_komentara++;
    					
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
        sucelje.prenesi_getcomments(postIds,messages,urlovi_u_poruci,timestamps,userIds,names,lastnames,pictures,usernames,liked,likesnum,br_komentara,error_info);
    }
    
    
       
    
    public interface prenesi{
    	void prenesi_getcomments(List<String> postIds, List<String> messages, List<String> urlovi, List<String> timestamps, List<String> userIds, List<String> names, List<String> lastnames,List<Drawable> pictures, List<String> usernames, List<Boolean> liked, List<String> likesnum, int broj_komentara, String error);
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