package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ferbook.InboxFragment.InboxAdapter;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity implements Search.prenesi{
	
	ArrayList<HashMap<String,?>> data;
	UserListAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    setContentView(R.layout.newsfeed);
	    data = new ArrayList<HashMap<String,?>>();
	   	Intent pozivajuca = getIntent();
	   	if(!Intent.ACTION_SEARCH.equals(pozivajuca.getAction())){
	   		Toast.makeText(this, "Search error", Toast.LENGTH_SHORT).show();
	   	}
	   	else{
	   		String upit = pozivajuca.getStringExtra(SearchManager.QUERY);
	   		getActionBar().setTitle("Search: "+upit);
	   		getActionBar().setDisplayHomeAsUpEnabled(true);
	   		adapter = new UserListAdapter(this, data);
	   		ListView listview = (ListView) findViewById(R.id.list_wall);
	   		listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
            	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
					prebaci.putExtra("id_za_profil", (String) view.getTag());
					prebaci.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(prebaci);
					finish();
            	}
			});	
			if(upit.equals("All"))
				new Search().execute("",this);
			else
				new Search().execute(upit,this);
	   	}
	}

	@Override
	public void prenesi_search(List<String> userIds, List<String> names,
			List<String> lastnames, List<Drawable> profilePictures,
			List<String> usernames, int broj_ljudi, String error) {
		HashMap<String,Object> redak;
		if(error!=null)
			Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
		for(int i=0;i<broj_ljudi;i++){
			redak = new HashMap<String,Object>();
			redak.put("user_id", userIds.get(i));
			redak.put("name", names.get(i)+" "+lastnames.get(i));
			redak.put("picture", profilePictures.get(i));
			data.add(redak);
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	public class UserListAdapter extends BaseAdapter{
		private Context context;
		private ArrayList<HashMap<String,?>> lista;
		public UserListAdapter(Context app, ArrayList<HashMap<String, ?>> data){
			context=app;
			lista=data;
		}
		@Override
		public int getCount() {
			return lista.size();
		}

		@Override
		public Object getItem(int arg0) {
			return lista.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View redak = inflater.inflate(R.layout.search_layout, arg2, false);
			TextView user = (TextView) redak.findViewById(R.id.search_name);
			ImageView slika = (ImageView) redak.findViewById(R.id.search_image);
			ImageButton poruka = (ImageButton) redak.findViewById(R.id.search_send_message);
			user.setText(lista.get(arg0).get("name").toString());
			if(lista.get(arg0).get("picture")!=null)
				slika.setImageDrawable((Drawable)lista.get(arg0).get("picture"));
			redak.setTag(lista.get(arg0).get("user_id").toString());
			poruka.setTag(lista.get(arg0).get("user_id").toString()+"|"+lista.get(arg0).get("name").toString());
			poruka.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					String user_id = (String) arg0.getTag();
					Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
					String a[] = user_id.split("\\|");
					Log.e("search",a[0]+" "+a[1]);
					prebaci.putExtra("id_za_poruke", a[0]);
					prebaci.putExtra("ime_za_poruke", a[1]);
					prebaci.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(prebaci);
					finish();
				}
			});
			return redak;
		}
		
	}
}
