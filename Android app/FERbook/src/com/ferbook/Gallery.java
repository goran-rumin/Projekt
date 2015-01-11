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

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class Gallery extends AsyncTask<Object, Void, Void> {

	private String error_info=null;
	int br_slika=0;
	
	List<String> postIds = new ArrayList<String>();
	List<Drawable> slike = new ArrayList<Drawable>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"photos/gallery/index.php";
	
	
    protected Void doInBackground(Object... arg0) {
    	
    	String galleryId=(String) arg0[0];
    	sucelje = (prenesi) arg0[1];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("albumId", galleryId);    	
    	params.add(user);            	
    	
    	ServiceHandler sh = new ServiceHandler();    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	JSONArray data=new JSONArray();
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			data = jsonObj.getJSONArray("data");
    			data.get(0); //ako je data prazan ide se na catch i parsiranje errora
    			}	
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa data ne postoji:
    			Log.e("gallery", "data ne postoji");
    			e.printStackTrace();
    			try{
    				error_info="No pictures";		//ako je data s razlogom prazan
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    				
    			}
    			catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			return null;
    		}
    			
    		//{ "data" : [ {"postId" : id, "url" : url} , {second Image}...}
    			
    			JSONObject slika=new JSONObject();
    			br_slika=0;
    			String postId;
    			String url_slike;
    			
    			while(true){
    				try {
						slika = data.getJSONObject(br_slika);
						
						postId=slika.getString("postId");
						url_slike=slika.getString("url");
						
						//ako sve ovo gore uspije i ako nejde u catch:
						
						postIds.add(postId);
						slike.add(vrati_sliku(url_slike));
						
    					br_slika++;
    					Log.e("br_slika", Integer.toString(br_slika));
    					
					} catch (JSONException e) {
						//e.printStackTrace();
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
    	Log.e("prenosim", "prenosim_gallery");
        sucelje.prenesi_gallery(postIds, slike, br_slika,  error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_gallery(List<String> postIds, List<Drawable> slike, int broj_slika, String error);
    }

    
    public static Drawable vrati_sliku(String url) {				
	    try {
	        InputStream is = (InputStream) new URL(url).getContent();
	        Drawable d = Drawable.createFromStream(is, "src name");//u ovom slucaju "src name" netrebam
	        return d;
	    } catch (Exception e) {
	        return null;
	    }
	}


}//kako ovo izvesti? :/
//{ "data" : [{"userId": id, "name":name, "lastname":lastname, "lastMessage" : { "message":message, "senderId": sender, "timestamp":timestamp, "flag":flag}}, {second conversation}, {third conversation}, …, {nth conversation}] , "error" : [] }
