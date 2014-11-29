package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class InboxFragment extends Fragment implements Inbox.prenesi{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		static NavigationDrawerFragment ladica;
		static ArrayList<HashMap<String,?>> data;
		static SimpleAdapter adapter;
		static TextView nema_konv;
		static ProgressBar progres;
		
		public static InboxFragment newInstance(int sectionNumber, NavigationDrawerFragment ladica_view) {
			InboxFragment fragment = new InboxFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			ladica = ladica_view;   //referenca na ladicu, zbog prikaza menua
			data = new ArrayList<HashMap<String,?>>();
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
			ListView listview = (ListView) rootView.findViewById(R.id.inbox_list);
			nema_konv = (TextView) rootView.findViewById(R.id.inbox_conversations);
			progres = (ProgressBar) rootView.findViewById(R.id.inbox_progress);
			
			final FragmentManager fragmm = getFragmentManager();
			adapter = new SimpleAdapter(getActivity(),
					data,
					R.layout.inbox_layout,
					new String[] {"inbox_item_sender","inbox_item_message","inbox_item_time"},
					new int[] { R.id.inbox_item_sender, R.id.inbox_item_message,R.id.inbox_item_time});
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
            	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					fragmm.beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), 
							MessageFragment.newInstance(2, data.get(position).get("inbox_userId").toString(), data.get(position).get("inbox_item_sender").toString()), "MessageFragment").addToBackStack("prebacivanje").commit();
            	}
			});	
			new Inbox().execute(Vrati_id.vrati(getActivity()), this);
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
				progres.setVisibility(View.VISIBLE);
				new Inbox().execute(Vrati_id.vrati(getActivity()), this);
				Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
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

		@Override
		public void prenesi_inbox(List<String> userIds, List<String> names,
				List<String> lastnames, List<String> messages,
				List<String> SenderIds, List<String> timestamps,
				List<Integer> flags, int broj_ljudi, String error) {
			HashMap<String,String> redak;
			progres.setVisibility(View.INVISIBLE);
			data.clear();
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			if(broj_ljudi!=0)
				nema_konv.setVisibility(View.GONE);
			for(int i=0;i<broj_ljudi;i++){
				redak = new HashMap<String,String>();
				redak.put("inbox_item_sender", names.get(i)+" "+lastnames.get(i));
				redak.put("inbox_item_message", messages.get(i));
				redak.put("inbox_item_time", timestamps.get(i));
				redak.put("inbox_userId", userIds.get(i));
				data.add(redak);
			}
			adapter.notifyDataSetChanged();
		}
}
