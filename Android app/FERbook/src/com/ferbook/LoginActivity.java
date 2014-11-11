package com.ferbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener, Login.prenesi{
	public String nesto;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
		
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(openFileInput("id.txt")));
			String id = bf.readLine();
			Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
			prebaci.putExtra("id", id);
			startActivity(prebaci);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	ProgressDialog pd;
	@Override
	public void onClick(View arg0) {
		EditText username = (EditText) findViewById(R.id.username);
		EditText password = (EditText) findViewById(R.id.password);
		if(provjeri_vezu()==1){
			pd = ProgressDialog.show(this, "", "Login in progress", true, true); 
			new Login().execute(username.getText().toString(), password.getText().toString(), this);
		}
	}
	@Override
	public void prenesi_login(String id, String error) {
		pd.dismiss();
		Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
		prebaci.putExtra("id", id);
		startActivity(prebaci);
	}
	/**
	 * Provjerava da li postoji veza na internet. Provjerava se WiFi i mobilni pristup.
	 * Ispisuje poruku o nepostojanju veze
	 * @return
	 * 1 ako postoji, 0 ako ne
	 */
	int provjeri_vezu(){
    	ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
    			||  conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
    		return 1;
    	}
    	else {
    		Toast.makeText(this, "Internet connection not available", Toast.LENGTH_SHORT).show();
    		return 0;
    	}
    }
}
