package com.ferbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity implements View.OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
		startActivity(prebaci);
	}
	
}
