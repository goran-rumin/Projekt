package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NewsfeedFragment extends Fragment implements Newsfeed.prenesi, Like.prenesi, OnScrollListener{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		static ArrayList<HashMap<String,Object>> data;
		WallListAdapter adapter;
		ProgressDialog pd;
		ListView listview;
		View footer;
		int trenutacni_index=1;
		
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
			View rootView = inflater.inflate(R.layout.newsfeed, container,
					false);
			listview = (ListView) rootView.findViewById(R.id.list_wall);
			adapter = new WallListAdapter(getActivity(),(Fragment) this,data);
			footer = getActivity().getLayoutInflater().inflate(R.layout.footer, null);
			listview.addFooterView(footer);
			listview.setAdapter(adapter);
			listview.setOnScrollListener(this);
			return rootView;
		}

		@Override
		public void onStart(){
			super.onStart();
			pd = ProgressDialog.show(getActivity(), "", "Please wait...", true, true);
			new Newsfeed().execute(Vrati_id.vrati(getActivity()), trenutacni_index, (Integer) Newsfeed.NEWS, this, getActivity()); //userId je String
		}
		
		@Override
		public void onPause(){
			data.clear();
			trenutacni_index=1;
			super.onPause();
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}

		@Override
		public void prenesi_newsfeed(List<String> postIds, List<String> texts,
				List<Drawable> urlovi_u_postu, List<String> timestamps,
				List<String> senderIds, List<String> senderNames,
				List<String> senderLastnames, List<Drawable> senderPictures,
				List<String> senderUsernames, List<String> senderEmails,
				List<String> recipientIds, List<String> recipientNames,
				List<String> recipientLastnames,
				List<Drawable> recipientPictures,
				List<String> recipientUsernames, List<String> recipientEmails,
				List<Boolean> liked_lista_boolean, List<Integer> broj_likeova,
				String error_info) {
			if(error_info!=null){
				Toast.makeText(getActivity(), error_info, Toast.LENGTH_SHORT).show();
				listview.removeFooterView(footer);
			}
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
				redak.put("news_item_likesnum","Likes: "+broj_likeova.get(i).toString());
				redak.put("news_item_like",liked_lista_boolean.get(i));
				redak.put("senderId", senderIds.get(i));
				redak.put("recipientId", recipientIds.get(i));
				data.add(redak);
			}
			pd.dismiss();
			adapter.notifyDataSetChanged();
			trenutacni_index+=1;
		}

		@Override
		public void prenesi_like(String action, String error, View v) {
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			else{
				RelativeLayout roditelj = (RelativeLayout) v.getParent();
				TextView likeovi = null;
				int trenutacni_broj=0;
				int djeca = roditelj.getChildCount();
				for(int i=0;i<djeca;i++){
					if(roditelj.getChildAt(i).getId()==R.id.news_item_likesnum || roditelj.getChildAt(i).getId()==R.id.comment_likes){
						likeovi = (TextView) roditelj.getChildAt(i);
						trenutacni_broj = Integer.parseInt(likeovi.getText().toString().split("\\ ")[1]);
						break;
					}
				}
				if(action.equals("like")){
					if(((Button) v).getId()==R.id.comment_likes){
						((Button) v).setText("Liked");
					}
					else{
						((Button) v).setText("   Liked    ");
					}
					likeovi.setText("Likes: "+(trenutacni_broj+1));
				}
				else{
					if(((Button) v).getId()==R.id.comment_likes){
						((Button) v).setText("Like");
					}
					else{
						((Button) v).setText("    Like    ");
					}
					likeovi.setText("Likes: "+(trenutacni_broj-1));
				}
			}
		}
		int prosli_zadnji=0;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				int zadnji = firstVisibleItem + visibleItemCount;
				if(zadnji == totalItemCount) {
					if(prosli_zadnji!=zadnji && trenutacni_index>1){
						new Newsfeed().execute(Vrati_id.vrati(getActivity()), trenutacni_index, (Integer) Newsfeed.NEWS, this, getActivity());
						prosli_zadnji = zadnji;
					}
				}	
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
}
