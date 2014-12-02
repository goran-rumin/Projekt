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

public class ImageAdapter extends BaseAdapter{
		private List<Drawable> pictures = new ArrayList<Drawable>();
        private LayoutInflater inflater;

        public ImageAdapter(Context c, List<Drawable> slike)
        {
            inflater = LayoutInflater.from(c);
            pictures = slike;
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Object getItem(int position)
        {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;

            if(v == null)
            {
               v = inflater.inflate(R.layout.gallery_item, viewGroup, false);
               v.setTag(R.id.picture, v.findViewById(R.id.picture));
            }

            picture = (ImageView)v.getTag(R.id.picture);

            //picture.setImageResource(myIds.get(position));
            picture.setImageDrawable(pictures.get(position));

            return v;
        }
}



/*  Zanemarite ovo
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
    private Context mContext;
    private List<Drawable> pictures = new ArrayList<Drawable>();

    public ImageAdapter(Context c, List<Drawable> slike) {
        mContext = c;
        pictures = slike;
    }

    public int getCount() {
        //return mThumbIds.length;
    	return pictures.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(pictures.get(position));
        imageView.setImageDrawable(pictures.get(position));
        return imageView;
    }   
}

*/

