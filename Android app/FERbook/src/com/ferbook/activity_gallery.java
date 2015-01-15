package com.ferbook;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * Ova aktivnost prikazuje pojedinacni album tj. prikazuje slike albuma.
 * Klikom na neku sliku pokrece se nova aktivnost koja sliku prikazuje fullscreen.
 * */

public class activity_gallery extends Activity implements Gallery.prenesi {
	
	final int ACTIVITY_CHOOSE_FILE = 1;
	
	public static final String EXTRA_GALLERY_ID = "com.ferbook.image_position";
	public static final String EXTRA_GALLERY_NAME = "com.ferbook.gallery_name";
	
	public static final String EXTRA_NEW_PICTURES = "com.ferbook.new_pictures";
	
	private String mGalleryId;
	private String mGalleryName;
	private GridView mGridView;
	private Boolean smije_objaviti = true;
	private ArrayList<Drawable> slike = new ArrayList<Drawable>();
	private ArrayList<Drawable> album = new ArrayList<Drawable>();
	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<Bitmap> nove_slike = new ArrayList<Bitmap>();
	
	private ProgressDialog mProgressDialog;
	private Boolean mProgressDialogShowed = false;
	private Boolean mNewPicturesUploaded = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		
		mGalleryId = getIntent().getStringExtra(EXTRA_GALLERY_ID);
		mGalleryName = getIntent().getStringExtra(EXTRA_GALLERY_NAME);
		
		this.setTitle(mGalleryName);
		
		mGridView = (GridView)findViewById(R.id.gridview);
		
		new Gallery().execute(mGalleryId, this);  

	}
	
	/*
	 * MENU
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.add_gallery, menu); //button za dodavanje albuma
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_album: //korisnik zeli dodati slike u album
					Intent i = new Intent(this, activity_addPicturesToAlbum.class);
					i.putExtra(activity_gallery.EXTRA_GALLERY_ID, mGalleryId);
					startActivityForResult(i, 12);
					return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * GALLERY
	 * Dohvati galeriju i prikazi ju i postavi listener
	 * */
	@Override
	public void prenesi_gallery(final List<String> postIds, List<Drawable> slike, int broj_slika, String error) {	
		mProgressDialog.dismiss();
		mProgressDialogShowed = true;
		if (error == null) {
			for (int i = 0; i < broj_slika; i++) {
				album.add(slike.get(i));
				ids.add(postIds.get(i));
				mGridView.setAdapter(new GalleryAdapter(this, slike));
			}
		} else {
			Toast.makeText(this, "Greska, pokusajte ponovo.", Toast.LENGTH_SHORT).show();
		}
		
		mGridView.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	Intent i = new Intent(activity_gallery.this, activity_fullscreen_image.class);
				i.putExtra(activity_fullscreen_image.EXTRA_POST_ID, ids.get(position));
				startActivity(i);
		    }
		});
	}
	
	/*
	 * 	ONSTART
	 * */
	@Override
    public void onStart(){
    	super.onStart();
    	Log.d("denis", "onStart");
    	if (!mProgressDialogShowed)
    		mProgressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
    }
	
	/*
	 * 	ONACTIVITYRESULT
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.d("denis", "onActivityResult");
    	if (data == null) {
    		return;
    	}
    	mNewPicturesUploaded = data.getBooleanExtra(activity_gallery.EXTRA_NEW_PICTURES, false);
    	Log.d("denis", "" + mNewPicturesUploaded);
    	if (mNewPicturesUploaded) {
    		Log.d("denis", "Nove slike su tu");
    		mProgressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
    		new Gallery().execute(mGalleryId, this); 
    	} else {
    		Log.d("denis", "Stare slike");
    	}
    }
	
	
	/*
	 * ADAPTER
	 * */
	public class GalleryAdapter extends BaseAdapter{
		
		private List<Drawable> pictures = new ArrayList<Drawable>();
        private LayoutInflater inflater;

        public GalleryAdapter(Context c, List<Drawable> slike)
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
        	
            ImageView picture;

            if(view == null)
            {
            	view = inflater.inflate(R.layout.image_layout, viewGroup, false);
            	view.setTag(R.id.picture, view.findViewById(R.id.picture));
            }

            picture = (ImageView)view.getTag(R.id.picture);

            //picture.setImageResource(myIds.get(position));
            picture.setImageDrawable(pictures.get(position));

            return view;
        }
	}
}
