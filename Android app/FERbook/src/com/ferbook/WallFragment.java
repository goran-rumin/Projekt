package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class WallFragment extends Fragment implements Newsfeed.prenesi{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		static ArrayList<HashMap<String,Object>> data;
		WallListAdapter adapter;
		ProgressDialog pd;
		
		public static WallFragment newInstance(int sectionNumber) {
			WallFragment fragment = new WallFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			
			//data = new ArrayList<HashMap<String,Object>>();
			return fragment;
		}

		public WallFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.wall, container,
					false);
			data = new ArrayList<HashMap<String,Object>>();
			ListView listview = (ListView) rootView.findViewById(R.id.list_wall);
			adapter = new WallListAdapter(getActivity(),(Fragment) this,data);
			listview.setAdapter(adapter);
			return rootView;
		}

		@Override
		public void onStart(){
			super.onStart();
			pd = ProgressDialog.show(getActivity(), "", "Please wait...", true, true);
			new Newsfeed().execute((Integer) 1, (Integer) Newsfeed.NEWS, this, getActivity());
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			/*((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));*/
		}

		@Override
		public void prenesi_newsfeed(List<String> postIds, List<String> texts,
				List<String> urlovi_u_postu, List<String> timestamps,
				List<String> senderIds, List<String> senderNames,
				List<String> senderLastnames, List<Drawable> senderPictures,
				List<String> senderUsernames, List<String> senderEmails,
				List<String> recipientIds, List<String> recipientNames,
				List<String> recipientLastnames,
				List<Drawable> recipientPictures,
				List<String> recipientUsernames, List<String> recipientEmails,
				List<Boolean> liked_lista_boolean, List<Integer> broj_likeova,
				String error_info) {
			if(error_info!=null)
				Toast.makeText(getActivity(), error_info, Toast.LENGTH_SHORT).show();
			for(int i=0;i<postIds.size();i++){
				HashMap<String,Object> redak = new HashMap<String,Object>();
				redak.put("news_item_pimage",senderPictures.get(i));
				redak.put("news_item_p2image",recipientPictures.get(i));
				redak.put("news_item_ptext",senderNames.get(i)+" "+senderLastnames.get(i));  //zavrsiti kad se zavrsi newsfeed do kraja
				redak.put("news_item_p2text",recipientNames.get(i)+" "+recipientLastnames.get(i));
				redak.put("news_item_text",texts.get(i));
				redak.put("news_item_image",urlovi_u_postu.get(i));
				redak.put("news_item_timestamp",timestamps.get(i));
				redak.put("news_item_likesnum",broj_likeova.get(i).toString());
				redak.put("news_item_like",liked_lista_boolean.get(i));
				redak.put("senderId", senderIds.get(i));
				redak.put("recipientId", recipientIds.get(i));
				data.add(redak);
			}
			pd.dismiss();
			adapter.notifyDataSetChanged();
		}
}