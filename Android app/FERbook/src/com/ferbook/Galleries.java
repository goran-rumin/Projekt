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

public class Galleries extends AsyncTask<Object, Void, Void> {
	
	private String error_info=null;
	private String albumId=null, name=null, url_slike=null;
	
	private int br_galerija=0;
	
		
	List<String> albumIds = new ArrayList<String>();
	List<String> names = new ArrayList<String>();
	List<Drawable> naslovnice = new ArrayList<Drawable>();
	
	
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = Vrati_id.ROOT+"user/galleries/index.php";
	
	
    protected Void doInBackground(Object... arg0) {    //(userId, sucelje )
    	//String postId=(String) arg0[0];

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
    				error_info="No galleries";		//ako je data s razlogom prazan
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    			}catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			return null;
    		}
    			
    			
    		JSONObject gal=new JSONObject();
    		
    		br_galerija=0;
    			
    			while(true){
    				try {
						gal = data.getJSONObject(br_galerija);
						
						albumId=gal.getString("albumId");
						name=gal.getString("name");
						url_slike=gal.getString("url");
						
						//ako sve ovo gore uspije i ako nejde u catch:
						
						albumIds.add(albumId);
						names.add(name);
						naslovnice.add(vrati_sliku(url_slike));
											
									
						
    					br_galerija++;
    					
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
        sucelje.prenesi_getlikes(albumIds, names, naslovnice, br_galerija,error_info);
    }
    
    
       
    
    public interface prenesi{
    	void prenesi_getlikes(List<String> albumIds, List<String> names, List<Drawable> naslovnice, int broj_galerija, String error);
    }

    
    public static Drawable vrati_sliku(String url) {
	    try {
	        InputStream is = (InputStream) new URL(url).getContent();
	        Drawable d = Drawable.createFromStream(is, "src name");
	        return d;
	    } catch (Exception e) {
	        return null;
	    }
	}


}