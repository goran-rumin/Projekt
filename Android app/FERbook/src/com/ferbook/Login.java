package com.ferbook;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



public class Login extends AsyncTask<Object, Void, String> {
	
	private String error_info=null;
	private prenesi sucelje;
	Activity kontekst;
	private static String url = "http://vdl.hr/ferbook/user/login/index.php";
	
	
    protected String doInBackground(Object... arg0) {
    	String username=(String) arg0[0];
    	String pass = (String) arg0[1];
    	sucelje = (prenesi) arg0[2];
    	kontekst = (LoginActivity) arg0[2];
    	List<NameValuePair> params= new ArrayList<NameValuePair>();
    	
    	NameValuePair user=new BasicNameValuePair("username", username);
    	NameValuePair pas=new BasicNameValuePair("password", pass);
    	
    	params.add(user);
    	params.add(pas);
        
    	String id=null;
    	
    	ServiceHandler sh = new ServiceHandler();
    	
    	String jsonStr = sh.makeServiceCall(url,  ServiceHandler.POST, params);
    	
    	if(jsonStr != null){
    		try{
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			JSONObject data = jsonObj.getJSONObject("data");
    			//JSONArray data = jsonObj.getJSONArray("data");
    			
    			id = data.getString("userId");
    			spremi_id(id);
    		}
    		catch(JSONException e){
    			//if the mapping doesn't exist, tj, ako je data prazan pa userid ne postoji:
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
    	
    	
     return id;
    }

    protected void onProgressUpdate(Integer... progress) {
        
    }

    protected void onPostExecute(String id) {
        sucelje.prenesi_login(id, error_info);
    }
    
    private void spremi_id(String id){
    	try {
    		OutputStreamWriter out = new OutputStreamWriter(kontekst.openFileOutput("id.txt", Context.MODE_PRIVATE));
    		out.write(id);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    
    public interface prenesi{
    	void prenesi_login(String id, String error);
    }



}
