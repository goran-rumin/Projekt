package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NewsfeedFragment extends Fragment implements Newsfeed.prenesi, Like.prenesi{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		static ArrayList<HashMap<String,Object>> data;
		WallListAdapter adapter;
		ProgressDialog pd;
		
		public static NewsfeedFragment newInstance(int sectionNumber) {
			NewsfeedFragment fragment = new NewsfeedFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			
			data = new ArrayList<HashMap<String,Object>>();
			return fragment;
		}

		public NewsfeedFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.wall, container,
					false);
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
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
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
				redak.put("news_item_pid", postIds.get(i));
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
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("news_item_pid", "1");
			redak.put("news_item_pimage",getResources().getDrawable(R.drawable.ic_launcher));
			redak.put("news_item_p2image",getResources().getDrawable(R.drawable.ic_launcher));
			redak.put("news_item_ptext","User1");  //zavrsiti kad se zavrsi newsfeed do kraja
			redak.put("news_item_p2text","User2");
			redak.put("news_item_text","Probni post");
			redak.put("news_item_image",getResources().getDrawable(R.drawable.more));
			redak.put("news_item_timestamp","2014-12-05 10:10:05");
			redak.put("news_item_likesnum","Likes: 5");
			redak.put("news_item_like",(Boolean) true);
			redak.put("senderId", ""+1);
			redak.put("recipientId", ""+3);
			data.add(redak);
			redak = new HashMap<String,Object>();
			redak.put("news_item_pid", "5");
			redak.put("news_item_pimage",getResources().getDrawable(R.drawable.ic_launcher));
			redak.put("news_item_p2image",getResources().getDrawable(R.drawable.ic_launcher));
			redak.put("news_item_ptext","User2");  //zavrsiti kad se zavrsi newsfeed do kraja
			redak.put("news_item_p2text","User1");
			redak.put("news_item_text","Probni post2");
			redak.put("news_item_image",getResources().getDrawable(R.drawable.more));
			redak.put("news_item_timestamp","2014-12-05 10:10:05");
			redak.put("news_item_likesnum","Likes: 1");
			redak.put("news_item_like",(Boolean) false);
			redak.put("senderId", ""+3);
			redak.put("recipientId", ""+1);
			data.add(redak);
			pd.dismiss();
			adapter.notifyDataSetChanged();
		}

		@Override
		public void prenesi_like(String action, String error, View v) {
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			else{
				if(action.equals("like"))
					((Button) v).setText("  Liked  ");
				else
					((Button) v).setText("  Like  ");
			}
		}
}
