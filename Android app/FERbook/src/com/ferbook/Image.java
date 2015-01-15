package com.ferbook;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ferbook.addGallery.prenesi;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class Image extends AsyncTask<Object, Void, Void> {
	private prenesi sucelje;
	private static String url=Vrati_id.ROOT+"photos/image/index.php";
	private String url_slike=null, error_info=null;
	Drawable slika;
	

	@Override
	protected Void doInBackground(Object... arg0) {
		NameValuePair user=new BasicNameValuePair("postId", (String) arg0[0]);
		sucelje = (prenesi) arg0[1];
		
		
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(user);
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			
    			url_slike=data.getString("url");
    			url_slike=url_slike.replace("ferbook.duckdns.org", "192.168.1.221");
    			slika=vrati_sliku(url_slike);
    		
    		}
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa url ne postoji:
    			e.printStackTrace();
    			try{
    				JSONObject jsonObj = new JSONObject(jsonStr);
    				JSONObject error = jsonObj.getJSONObject("error");
    				error_info=error.getString("errInfo");
    				
    			}
    			catch(JSONException e1){
    				e1.printStackTrace();
    				//System.out.print("greška u parsiranju");
    			}
    			
    		}
    	}
    	else error_info="DB does not respond";
    	
		
		return null;
	}
	
	protected void onPostExecute(Void param) {
	        sucelje.prenesi_image(slika ,error_info);	
	        slika=null;
	    }
	
	public interface prenesi{
    	void prenesi_image(Drawable slika, String error);	//then set image to imageview using code in your activity.
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
