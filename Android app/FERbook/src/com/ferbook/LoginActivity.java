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
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener, Login.prenesi{  //3. korisnik probni 1234
	public String nesto;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		TextView reg = (TextView) findViewById(R.id.reg);
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		String id = Vrati_id.vrati(this);
		if(id!=null){
			Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
			prebaci.putExtra("id", id);
			startActivity(prebaci);
		}
		else{
			reg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent pogledaj = new Intent(Intent.ACTION_VIEW);
					pogledaj.setData(Uri.parse("http://ferbook.duckdns.org"));
					startActivity(pogledaj);
				}
			});
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
		if(error==null){
			Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
			prebaci.putExtra("id", id);
			startActivity(prebaci);
		}
		else
			Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
