package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Gallery extends AsyncTask<Object, Void, String> {

	private String error_info=null;
	int br_slika=0;
	
	List<String> postIds = new ArrayList<String>();
	List<String> url_slika = new ArrayList<String>();
	
	private prenesi sucelje;
	//Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/photos/galley/index.php";
	
	
    protected String doInBackground(Object... arg0) {
    	
    	String galleryId=(String) arg0[0];
    	sucelje = (prenesi) arg0[1];
    	
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("galleryId", galleryId);    	
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
						url_slika.add(url_slike);
						
    					br_slika++;
    					
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

    protected void onPostExecute() {
        sucelje.prenesi_gallery(postIds, url_slika, br_slika,  error_info);
    }
    
    
    
    
    
    public interface prenesi{
    	void prenesi_gallery(List<String> postIds, List<String> url_slika, int broj_slika, String error);
    }



}//kako ovo izvesti? :/
//{ "data" : [{"userId": id, "name":name, "lastname":lastname, "lastMessage" : { "message":message, "senderId": sender, "timestamp":timestamp, "flag":flag}}, {second conversation}, {third conversation}, …, {nth conversation}] , "error" : [] }
