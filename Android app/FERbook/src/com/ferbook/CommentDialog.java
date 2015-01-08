package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CommentDialog extends Dialog implements GetComments.prenesi, GetLikes.prenesi, OnClickListener, Comment.prenesi{

	public static int TYPE_COMMENT = 1;
	public static int TYPE_SHOW_LIKES = 2;
	private String post_id;
	private Context kontekst;
	private Fragment h;
	private ListView listview;
	private TextView naslov;
	private CommentsAdapter adapter;
	private SimpleAdapter adapter2;
	private EditText poruka;
	private ArrayList<HashMap<String,?>> data;
	
	public CommentDialog(Context context, int tip, String post, Fragment host) {
		super(context);
		kontekst=context;
		post_id = post;
		data = new ArrayList<HashMap<String,?>>();
		h=host;
		int dividerId = getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		findViewById(dividerId).setBackgroundColor(getContext().getResources().getColor(R.color.glavna_boja));
		if(tip==TYPE_COMMENT){
			setTitle(Html.fromHtml("<font color='#B22222'>Comment</font>"));
			setContentView(R.layout.comments);
			
			naslov = (TextView) findViewById(R.id.comments_text);
			listview = (ListView) findViewById(R.id.list_comments);
			adapter = new CommentsAdapter(kontekst,data, this);
			listview.setAdapter(adapter);
			new GetComments().execute(post,this,kontekst);
			poruka = (EditText) findViewById(R.id.comment_text);
			Button send = (Button) findViewById(R.id.send);
			send.setOnClickListener(this);
		}
		else if(tip==TYPE_SHOW_LIKES){
			setTitle(Html.fromHtml("<font color='#B22222'>Likes</font>"));
			setContentView(R.layout.newsfeed);
			listview = (ListView) findViewById(R.id.list_wall);
			adapter2 = new SimpleAdapter(kontekst,
					data,
					R.layout.likes_layout,
					new String[] {"ime","slika"},
					new int[] { R.id.likes_name, R.id.likes_image});
			adapter2.setViewBinder(pov);
			listview.setAdapter(adapter2);
			new GetLikes().execute(post,this, kontekst);
		}
		
	}

	@Override
	public void prenesi_getcomments(List<String> postIds,
			List<String> messages, List<String> urlovi,
			List<String> timestamps, List<String> userIds, List<String> names,
			List<String> lastnames, List<Drawable> pictures,
			List<String> usernames, int broj_komentara, String error) {
		if(error!=null)
			Toast.makeText(kontekst, error, Toast.LENGTH_SHORT).show();
		else
			naslov.setVisibility(View.GONE);
		for(int i=0;i<postIds.size();i++){
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("post_id", postIds.get(i));
			redak.put("picture",pictures.get(i));
			redak.put("comment",messages.get(i));
			redak.put("name",names.get(i)+" "+lastnames.get(i));
			redak.put("timestamp",timestamps.get(i));
			redak.put("likes","Likes: "+0);
			data.add(redak);
		}
		adapter.notifyDataSetChanged();
	}
	
	View.OnClickListener listener = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			Toast.makeText(kontekst, "kliknuto", Toast.LENGTH_SHORT).show();
			new Like().execute(Vrati_id.vrati((Activity)kontekst),post_id,h,v);
		}
	};
	
	private final SimpleAdapter.ViewBinder pov =
			new SimpleAdapter.ViewBinder() {  //za prikaz drawablea pomocu simpleadaptera da se ne pise adapter za jednostavnu listu
				@Override
				public boolean setViewValue(
						final View view,
						final Object data,
						final String textRepresentation) {

					if (view instanceof ImageView) {
						((ImageView) view).setImageDrawable((Drawable) data);
						return true;
					}

					return false;
		        }
		  	};
	
	public class CommentsAdapter extends BaseAdapter{
		private Context context;
		private ArrayList<HashMap<String,?>> lista;
		private Dialog impl;
		public CommentsAdapter(Context app, ArrayList<HashMap<String, ?>> data, Dialog sucelje){
			context=app;
			lista=data;
			impl=sucelje;
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
			View redak = inflater.inflate(R.layout.comment_layout, arg2, false);
			
			TextView user = (TextView) redak.findViewById(R.id.comment_name);
			ImageView slika = (ImageView) redak.findViewById(R.id.comment_image);
			Button like = (Button) redak.findViewById(R.id.comment_like);
			TextView timestamp = (TextView) redak.findViewById(R.id.comment_timestamp);
			TextView komentar = (TextView) redak.findViewById(R.id.comment);
			TextView likesnum = (TextView) redak.findViewById(R.id.comment_likes);
			
			user.setText(lista.get(arg0).get("name").toString());
			if(lista.get(arg0).get("picture")!=null)
				slika.setImageDrawable((Drawable)lista.get(arg0).get("picture"));
			komentar.setText(lista.get(arg0).get("comment").toString());
			timestamp.setText(lista.get(arg0).get("timestamp").toString());
			likesnum.setText(lista.get(arg0).get("likes").toString());
			
			like.setTag(lista.get(arg0).get("post_id").toString());
			
			like.setOnClickListener(listener);
			return redak;
		}
		
	}

	@Override
	public void prenesi_getlikes(List<String> timestamps,
			List<String> userIds, List<String> names, List<String> lastNames,
			List<Drawable> pictures, List<String> usernames,
			List<String> emails, int broj_likeova, String error) {
		if(error!=null)
			Toast.makeText(kontekst, error, Toast.LENGTH_SHORT).show();
		for(int i=0;i<broj_likeova;i++){
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("slika",pictures.get(i));
			redak.put("ime",names.get(i)+" "+lastNames.get(i));
			data.add(redak);
		}
		if(data.isEmpty()){
			TextView tv1 = new TextView(kontekst);
			tv1.setGravity(Gravity.CENTER_HORIZONTAL);
			tv1.setText("No likes");
			setContentView(tv1);
		}
		adapter2.notifyDataSetChanged();
	}

	@Override
	public void onClick(View arg0) {
		String komentar = poruka.getText().toString();
		new Comment().execute(Vrati_id.vrati((Activity)kontekst), post_id, komentar, this);
		poruka.setText("");
	}

	@Override
	public void prenesi_comment(String commentId, String error) {
		if(error!=null)
			Toast.makeText(kontekst, error, Toast.LENGTH_SHORT).show();
		else{
			data.clear();
			new GetComments().execute(post_id,this,kontekst);
		}
	}
}
