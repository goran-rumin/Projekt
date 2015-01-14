package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

	public class WallListAdapter extends BaseAdapter{
		private final Context context;
		private Fragment host;
		private ArrayList<HashMap<String, Object>> values;
		Button like;

		public WallListAdapter(Context context, Fragment fr, ArrayList<HashMap<String, Object>> data) {
			this.context = context;
			this.values = data;
			this.host = fr;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			HashMap<String,Object> redak = values.get(position);
			View rowView = inflater.inflate(R.layout.news_layout, parent, false);
			ImageView profilna = (ImageView) rowView.findViewById(R.id.news_item_pimage);
			ImageView profilna2 = (ImageView) rowView.findViewById(R.id.news_item_p2image);
			TextView ime_korisnika = (TextView) rowView.findViewById(R.id.news_item_ptext);
			TextView ime_korisnika2 = (TextView) rowView.findViewById(R.id.news_item_p2text);
			TextView tekst = (TextView) rowView.findViewById(R.id.news_item_text);
			ImageView slika = (ImageView) rowView.findViewById(R.id.news_item_image);
			like = (Button) rowView.findViewById(R.id.news_item_like);
			Button comment = (Button) rowView.findViewById(R.id.news_item_comment);
			TextView broj_likeova = (TextView) rowView.findViewById(R.id.news_item_likesnum);
			TextView timestamp = (TextView) rowView.findViewById(R.id.news_item_timestamp);
			
			profilna.setImageDrawable((Drawable)redak.get("news_item_pimage"));
			profilna2.setImageDrawable((Drawable)redak.get("news_item_p2image"));
			ime_korisnika.setText((String)redak.get("news_item_ptext"));
			ime_korisnika2.setText((String)redak.get("news_item_p2text"));
			tekst.setText((String)redak.get("news_item_text"));
			slika.setImageDrawable((Drawable)redak.get("news_item_image"));  //asynctask vraca urlove
			broj_likeova.setText((String)redak.get("news_item_likesnum"));
			timestamp.setText((String)redak.get("news_item_timestamp"));
			
			ime_korisnika.setTag((String)redak.get("senderId"));
			profilna.setTag((String)redak.get("senderId"));
			ime_korisnika2.setTag((String)redak.get("recipientId"));
			profilna2.setTag((String)redak.get("recipientId"));
			broj_likeova.setTag(redak.get("news_item_pid"));
			comment.setTag(redak.get("news_item_pid"));
			
			if((Boolean)redak.get("news_item_like").equals(true))
				like.setText("   Liked    ");
			like.setTag(redak.get("news_item_pid"));
			ime_korisnika.setOnClickListener(listener);
			profilna.setOnClickListener(listener);
			ime_korisnika2.setOnClickListener(listener);
			profilna2.setOnClickListener(listener);
			
			slika.setTag(redak.get("news_item_pid"));
			slika.setOnClickListener(listener2);
			
			broj_likeova.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommentDialog dialog = new CommentDialog(context,CommentDialog.TYPE_SHOW_LIKES, (String) v.getTag(), host);
					dialog.show();
				}
			});
			
			comment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommentDialog dialog = new CommentDialog(context,CommentDialog.TYPE_COMMENT, (String) v.getTag(), host);
					dialog.show();
				}
			});
			
			like.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new Like().execute(Vrati_id.vrati((Activity) context),v.getTag(),host, v);
				}
				
			});
			return rowView;
		}

		@Override
		public int getCount() {
			return values.size();
		}

		@Override
		public Object getItem(int arg0) {
			return values.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		View.OnClickListener listener = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent prebaci = new Intent(((Activity) context).getBaseContext(), MainActivity.class);
				prebaci.putExtra("id_za_profil", (String) v.getTag());
				prebaci.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				((Activity) context).startActivity(prebaci);
			}
		};
		
		View.OnClickListener listener2 = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent prebaci = new Intent(((Activity) context).getBaseContext(), activity_fullscreen_image.class);
				prebaci.putExtra("com.ferbook.image_position", (String) v.getTag());
				((Activity) context).startActivity(prebaci);
			}
		};
	} 
