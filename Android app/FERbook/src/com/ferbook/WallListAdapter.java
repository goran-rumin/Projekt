package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

	public class WallListAdapter extends BaseAdapter {
		private final Context context;
		private ArrayList<HashMap<String, Object>> values;

		public WallListAdapter(Context context, ArrayList<HashMap<String, Object>> data) {
			this.context = context;
			this.values = data;
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
			Button like = (Button) rowView.findViewById(R.id.news_item_like);
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
			ime_korisnika2.setTag((String)redak.get("recipientId"));
			ime_korisnika.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "Kliknuto "+v.getTag(), Toast.LENGTH_SHORT).show();
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
	} 
