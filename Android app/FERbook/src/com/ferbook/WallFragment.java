package com.ferbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WallFragment extends Fragment implements Newsfeed.prenesi, OnScrollListener, OnClickListener, Upload.prenesi, Publish.prenesi, Like.prenesi{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara
		final int ACTIVITY_CHOOSE_FILE = 1;
		
		static ArrayList<HashMap<String,Object>> data;
		WallListAdapter adapter;
		ProgressDialog pd;
		ListView listview;
		View footer;
		String user_id;
		EditText post_text;
		Button post_send, post_upload;
		String slika_url = null;
		boolean smije_objaviti=true;
		int trenutacni_index=1;
		Bitmap slika = null;
		
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
			listview = (ListView) rootView.findViewById(R.id.list_wall);
			adapter = new WallListAdapter(getActivity(),(Fragment) this,data);
			footer = getActivity().getLayoutInflater().inflate(R.layout.footer, null);
			listview.addFooterView(footer);
			listview.setAdapter(adapter);
			listview.setOnScrollListener(this);
			
			user_id=getArguments().getString("userid");
			if(savedInstanceState!=null)
				user_id=savedInstanceState.getString("userid");
			if(user_id==null)
				user_id=Vrati_id.vrati(getActivity());
			
			post_text = (EditText) rootView.findViewById(R.id.post_text);
			post_send = (Button) rootView.findViewById(R.id.post_send);
			post_send.setOnClickListener(this);
			post_upload = (Button) rootView.findViewById(R.id.post_upload);
			post_upload.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v) {
					if(!smije_objaviti)
						Toast.makeText(getActivity(), "Picture is uploading. Please wait...", Toast.LENGTH_SHORT).show();
					else{
						Intent chooseFile;
						Intent intent;
						chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
						chooseFile.setType("image/jpeg");
						intent = Intent.createChooser(chooseFile, "Choose a picture");
						startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
					}
				}});
			return rootView;
		}

		@Override
		public void onResume(){
			super.onResume();
			pd = ProgressDialog.show(getActivity(), "", "Please wait...", true, true);
			new Newsfeed().execute(user_id, trenutacni_index, (Integer) Newsfeed.WALL, this, getActivity());
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
			/*((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));*/
		}

		@Override
		public void onSaveInstanceState(Bundle stanje){
			stanje.putString("userid", user_id);
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
		
		int prosli_zadnji=0;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				int zadnji = firstVisibleItem + visibleItemCount;
				if(zadnji == totalItemCount) {
					if(prosli_zadnji!=zadnji && trenutacni_index>1){
						new Newsfeed().execute(user_id, trenutacni_index, (Integer) Newsfeed.WALL, this, getActivity());
						prosli_zadnji = zadnji;
					}
				}	
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}

		@Override
		public void onClick(View arg0) {
			String post = post_text.getText().toString();
			if(slika!=null){
				smije_objaviti=false;
				Toast.makeText(getActivity(), "Picture is uploading. Please wait...", Toast.LENGTH_SHORT).show();
				new Upload().execute(Vrati_id.vrati(getActivity()),slika,null,post,this,user_id);
			}
			else{
				new Publish().execute(Vrati_id.vrati(getActivity()), user_id, post, null, this);
			}
			post_text.setText("");
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch(requestCode) {
				case ACTIVITY_CHOOSE_FILE:
					if (resultCode == Activity.RESULT_OK){
						Uri uri = data.getData();
						String put = "";
						slika = null;
						try {
							if(!uri.getPath().endsWith(".jpg") && !uri.getPath().endsWith(".jpeg")){
								put = getRealPathFromURI(uri);
							}
							else
								put=uri.getPath();
							if(!put.endsWith(".jpg") && !put.endsWith(".jpeg")){
								Toast.makeText(getActivity(), "Supported format is JPG", Toast.LENGTH_SHORT).show();
								break;
							}
							File file = new File(put);
							float velicina = file.length();
							if(velicina<1024*2014){
								slika = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
								Toast.makeText(getActivity(), "Picture selected", Toast.LENGTH_SHORT).show();
								//new Upload().execute(Vrati_id.vrati(getActivity()),slika,null,this);   //odkomentirati kad sve bude na serveru rijeseno
							}
							else{
								Toast.makeText(getActivity(), "Picture is too large", Toast.LENGTH_SHORT).show();
							}
						} catch (FileNotFoundException e) {
							Toast.makeText(getActivity(), "File name error. Try using gallery for selection", Toast.LENGTH_SHORT).show();
						} catch (IOException e) {
							Toast.makeText(getActivity(), "Supported format is JPG", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(getActivity(), "Supported format is JPG", Toast.LENGTH_SHORT).show();
						}
					}
					
					break;
				default:
					Toast.makeText(getActivity(), "File error", Toast.LENGTH_SHORT).show();
			}
		}
		
		public String getRealPathFromURI(Uri contentUri) throws Exception{

	        String [] proj={MediaStore.Images.Media.DATA};
	        Cursor cursor = getActivity().getContentResolver().query( contentUri,
	                        proj,
	                        null,   
	                        null,    
	                        null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();

	        return cursor.getString(column_index);
	}

		@Override
		public void prenesi_upload(String url_slike, String error) {
			Toast.makeText(getActivity(), "Upload finished", Toast.LENGTH_SHORT).show();
			slika=null;
			smije_objaviti=true;
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			else{
				data.clear();
				trenutacni_index=1;
				new Newsfeed().execute(user_id, trenutacni_index, (Integer) Newsfeed.WALL, this, getActivity());
			}
		}

		@Override
		public void prenesi_publish(String postId, String error) {
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			else{
				data.clear();
				trenutacni_index=1;
				pd = ProgressDialog.show(getActivity(), "", "Please wait...", true, true);
				new Newsfeed().execute(user_id, trenutacni_index, (Integer) Newsfeed.WALL, this, getActivity());
			}
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
					if(((Button) v).getId()==R.id.comment_likes)
						((Button) v).setText("Liked");
					else
						((Button) v).setText("   Liked    ");
					likeovi.setText("Likes: "+(trenutacni_broj+1));
				}
				else{
					if(((Button) v).getId()==R.id.comment_likes)
						((Button) v).setText("Like");
					else
						((Button) v).setText("    Like    ");
					likeovi.setText("Likes: "+(trenutacni_broj-1));
				}
			}
		}
}
