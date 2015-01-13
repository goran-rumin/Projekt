package com.ferbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class Upload extends AsyncTask<Object, Void, Void> {
	private prenesi sucelje;
	//private static String url="http://vdl.hr/ferbook/photos/upload/index.php";
	private static String url=Vrati_id.ROOT+"photos/upload/index.php";
	private String url_slike=null, error_info=null;
	Bitmap image;

	@Override
	protected Void doInBackground(Object... arg0) {
		String userId= (String) arg0[0];
		image=(Bitmap) arg0[1];
		String albumId= (String) arg0[2];	//OPTIONAL inače null
		String message= (String) arg0[3];  //opcionalno
		sucelje = (prenesi) arg0[4];
		String userId2 = (String) arg0[5];
		
		String imageStream=encodeTobase64(image);
		
		NameValuePair prvi=new BasicNameValuePair("userId", userId);
		NameValuePair treci=new BasicNameValuePair("userId2", userId2);
		NameValuePair drugi=new BasicNameValuePair("url", imageStream);
		
		List<NameValuePair> params= new ArrayList<NameValuePair>();
		params.add(prvi);
		params.add(drugi);
		params.add(treci);
		Log.e("slika2","*"+prvi+" "+drugi+"*");
		if(albumId!=null) params.add(new BasicNameValuePair("albumId", albumId));
		if(message!=null) params.add(new BasicNameValuePair("message", message));
		
		ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr =sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	Log.e("slika","*"+jsonStr+"*");
    	
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			
    			url_slike=data.getString("url");
    		
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
	        sucelje.prenesi_upload(url_slike ,error_info);	
	    }
	
	public interface prenesi{
    	void prenesi_upload(String url_slike, String error);
    }

	
	
	
	public static String encodeTobase64(Bitmap image)
	{
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

	    return imageEncoded;
	}
}
