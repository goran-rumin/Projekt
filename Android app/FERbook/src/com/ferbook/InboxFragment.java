package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class InboxFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		static NavigationDrawerFragment ladica;
		static ArrayList<HashMap<String,?>> data;
		
		public static InboxFragment newInstance(int sectionNumber, NavigationDrawerFragment ladica_view) {
			InboxFragment fragment = new InboxFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			ladica = ladica_view;   //referenca na ladicu, zbog prikaza menua
			
			data = new ArrayList<HashMap<String,?>>();
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("inbox_item_sender","TestUser1");
			redak.put("inbox_item_message","Kratka poruka");
			redak.put("inbox_item_time","14.11.2014. 22:00");
			data.add(redak);
			redak = new HashMap<String,Object>();
			redak.put("inbox_item_sender","TestUser2");
			redak.put("inbox_item_message","Duga poruka abababaabbababababababababbabababababababababababaabababba");
			redak.put("inbox_item_time","14.11.2014. 22:00");
			data.add(redak);
			return fragment;
		}

		public InboxFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.inbox, container,
					false);
			ListView listview = (ListView) rootView.findViewById(R.id.list_wall);
			SimpleAdapter adapter = new SimpleAdapter(getActivity(),
					data,
					R.layout.inbox_layout,
					new String[] {"inbox_item_sender","inbox_item_message","inbox_item_time"},
					new int[] { R.id.inbox_item_sender, R.id.inbox_item_message,R.id.inbox_item_time});
			listview.setAdapter(adapter);
			final FragmentManager fragmm = getFragmentManager();
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
            	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					fragmm.beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), 
							MessageFragment.newInstance(2, "neki UserId"), "MessageFragment").addToBackStack("prebacivanje").commit();
            	}
			});	
			return rootView;
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		    super.onCreateOptionsMenu(menu, inflater);
		    if(!ladica.isDrawerOpen())
		    	inflater.inflate(R.menu.inbox, menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.inbox_refresh) {
				Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
}
