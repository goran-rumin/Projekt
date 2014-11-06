package com.ferbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
	}
	ProgressDialog pd;
	@Override
	public void onClick(View arg0) {
		EditText username = (EditText) findViewById(R.id.username);
		EditText password = (EditText) findViewById(R.id.password);
		//pd = ProgressDialog.show(this, "", "Ulogiranje", true, true); 
		Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
		prebaci.putExtra("username", username.getText().toString());
		prebaci.putExtra("password", password.getText().toString());
		startActivity(prebaci);
	}
	
}
