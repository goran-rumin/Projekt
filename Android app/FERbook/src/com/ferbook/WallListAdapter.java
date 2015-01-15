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
import android.util.Log;
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
			View rowView = null;
			if(convertView!=null){
				if(convertView.getTag().equals("element")){
					Log.e("reciklaza", "radi");
					rowView=convertView;
				}
				else{
					rowView = inflater.inflate(R.layout.news_layout, parent, false);
					rowView.setTag("element");
				}
			}
			else{
				rowView = inflater.inflate(R.layout.news_layout, parent, false);
				rowView.setTag("element");
			}
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
			
			Drawable velika_slika=null;
			if(redak.get("news_item_image")!=null){
				String put = context.getCacheDir()+"/"+redak.get("news_item_image"); 
				velika_slika = Drawable.createFromPath(put);
			}
			
			
			profilna.setImageDrawable((Drawable)redak.get("news_item_pimage"));
			profilna2.setImageDrawable((Drawable)redak.get("news_item_p2image"));
			ime_korisnika.setText((String)redak.get("news_item_ptext"));
			ime_korisnika2.setText((String)redak.get("news_item_p2text"));
			tekst.setText((String)redak.get("news_item_text"));
			slika.setImageDrawable(velika_slika);  //asynctask vraca urlove
			velika_slika=null;
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
			else
				like.setText("    Like    ");
			like.setTag(redak.get("news_item_pid")+"|"+position);
			ime_korisnika.setOnClickListener(listener);
			profilna.setOnClickListener(listener);
			ime_korisnika2.setOnClickListener(listener);
			profilna2.setOnClickListener(listener);
			
			slika.setTag(redak.get("news_item_pid"));
			slika.setOnClickListener(listener2);
			
			broj_likeova.setOnClickListener(listener3);
			
			comment.setOnClickListener(listener4);
			
			like.setOnClickListener(listener5);
			
			profilna = null;
			profilna2 = null;
			ime_korisnika = null;
			ime_korisnika2 = null;
			tekst = null;
			slika = null;
			like = null;
			comment = null;
			broj_likeova = null;
			timestamp = null;
			
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
		
		View.OnClickListener listener3 = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				CommentDialog dialog = new CommentDialog(context,CommentDialog.TYPE_SHOW_LIKES, (String) v.getTag(), host);
				dialog.show();
			}
		};
		View.OnClickListener listener4 = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				CommentDialog dialog = new CommentDialog(context,CommentDialog.TYPE_COMMENT, (String) v.getTag(), host);
				dialog.show();
			}
		};
		View.OnClickListener listener5 = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				String a[] = ((String) v.getTag()).split("\\|");
				int trenutacni_broj = Integer.parseInt(((String)values.get(Integer.parseInt(a[1])).get("news_item_likesnum")).split("\\ ")[1]);
				if(((Button)v).getText().equals("   Liked    ")){
					values.get(Integer.parseInt(a[1])).put("news_item_likesnum", "Likes: "+(trenutacni_broj-1));
					values.get(Integer.parseInt(a[1])).put("news_item_like",false);
				}
				else{
					values.get(Integer.parseInt(a[1])).put("news_item_likesnum", "Likes: "+(trenutacni_broj+1));
					values.get(Integer.parseInt(a[1])).put("news_item_like",true);
				}
				new Like().execute(Vrati_id.vrati((Activity) context),a[0],host, v);
			}
		};
	} 
