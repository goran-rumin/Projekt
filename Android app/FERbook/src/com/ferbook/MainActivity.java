package com.ferbook;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	
	private NavigationDrawerFragment mNavigationDrawerFragment;

	private CharSequence mTitle;
	String user_id,user_id_poruke=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(Vrati_id.cache_sadrzaj==null)  //inicijalizacija indexa cachea
			Vrati_id.cache_sadrzaj=new ArrayList<String>();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		user_id = Vrati_id.vrati(this);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		
		Intent pokretanje_iz_pretrage = getIntent();  //da li je pokrenuto nakon pretrage
		if(pokretanje_iz_pretrage!=null)
			if(pokretanje_iz_pretrage.getStringExtra("id_za_poruke")!=null){  //pocetak za poruke
				final FragmentManager fragmm = getFragmentManager();
				user_id_poruke=pokretanje_iz_pretrage.getStringExtra("id_za_poruke");
				String ime_poruke=pokretanje_iz_pretrage.getStringExtra("ime_za_poruke");
				fragmm.beginTransaction().replace(R.id.container, 
						MessageFragment.newInstance(2, user_id_poruke, ime_poruke), "MessageFragment").addToBackStack("prebacivanje").commit();
			}
			else if(pokretanje_iz_pretrage.getStringExtra("id_za_profil")!=null){
				user_id=pokretanje_iz_pretrage.getStringExtra("id_za_profil");
				mNavigationDrawerFragment.prebaci_fragment(2);
			}
		
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.popBackStack();
		switch (position){
		case 0:
			user_id=Vrati_id.vrati(this);
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					NewsfeedFragment.newInstance(position + 1)).commit();
			break;
		case 1:
			user_id=Vrati_id.vrati(this);
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					InboxFragment.newInstance(position + 1, mNavigationDrawerFragment)).commit();
			break;
		case 2:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					ProfileFragment.newInstance(position + 1, user_id)).commit();
			break;
		case 3:
			user_id=Vrati_id.vrati(this);
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					fragment_galleries.newInstance(position + 1)).commit();
			break;
		default:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					PlaceholderFragment.newInstance(position + 1)).commit();
			break;
		}
	}

	public void onSectionAttached(int number) {
		String[] ladica = getResources().getStringArray(R.array.navigacijska_ladica);
		switch (number) {
		case 1:
			mTitle = ladica[0];
			break;
		case 2:
			mTitle = ladica[1];
			break;
		case 3:
			mTitle = ladica[2];
			break;
		case 4:
			mTitle = ladica[3];
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			SearchManager searchManager =
			           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			    SearchView searchView =
			            (SearchView) menu.findItem(R.id.action_example).getActionView();
			    searchView.setSearchableInfo(
			            searchManager.getSearchableInfo(getComponentName()));
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_example) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		MessageFragment f = (MessageFragment)getFragmentManager().findFragmentByTag("MessageFragment");
		if(f!=null && f.isVisible()){
			getFragmentManager().popBackStack();
			f.socket.disconnect();
			mNavigationDrawerFragment.prebaci_fragment(1);
		}
		else if(!user_id.equals(Vrati_id.vrati(this))){
			user_id=Vrati_id.vrati(this);
			mNavigationDrawerFragment.prebaci_fragment(0);
			/*Intent prebaci = new Intent(getBaseContext(), MainActivity.class);
			prebaci.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(prebaci);*/
		}
		else {
	        
			new AlertDialog.Builder(this)
	        .setTitle("Quit")
	        .setMessage("Are you sure?")
	        .setNegativeButton("Cancel", null)
	        .setPositiveButton("Yes", new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
	            	//brisanje cachea
	    			File dir = getBaseContext().getCacheDir();
	    			File[] files = dir.listFiles();
	    	        for (File file : files)
	    	            file.delete();
	    	            
	    	        Vrati_id.cache_sadrzaj=null;
	                MainActivity.super.onBackPressed();
	            }
	        }).create().show();
		}
			
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
}
