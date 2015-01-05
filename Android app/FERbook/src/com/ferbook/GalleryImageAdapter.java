package com.ferbook;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Ovo je adapter za prikaz elementa u fragment_galleries.
 * Element se sastoji od glavne slike albuma i naziva albuma.
 * */

public class GalleryImageAdapter extends BaseAdapter{
	
		private List<Drawable> pictures = new ArrayList<Drawable>();
		private List<String> galleryIds = new ArrayList<String>();
        private LayoutInflater inflater;

        public GalleryImageAdapter(Context c, List<Drawable> slike, List<String> ids){
            inflater = LayoutInflater.from(c);
            pictures = slike;
            galleryIds = ids;
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Object getItem(int position){
            return galleryIds.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }
        
        @Override
        public View getView(int position, View view, ViewGroup viewGroup){
        	
            ImageView picture;
            TextView album_name;

            if(view == null){
            	view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            	view.setTag(R.id.picture, view.findViewById(R.id.picture));
            	view.setTag(R.id.album_name, view.findViewById(R.id.album_name));
            }

            picture = (ImageView)view.getTag(R.id.picture);
            album_name = (TextView)view.getTag(R.id.album_name);

            //picture.setImageResource(myIds.get(position));
            picture.setImageDrawable(pictures.get(position));
            album_name.setText("Album " + (position + 1));

            return view;
        }
}

