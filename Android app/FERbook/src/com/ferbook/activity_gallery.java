package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
	
	private String mGalleryId;
	private GridView mGridView;
	private Boolean smije_objaviti = true;
	private ArrayList<Drawable> slike = new ArrayList<Drawable>();
	private ArrayList<Drawable> album = new ArrayList<Drawable>();
	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<Bitmap> nove_slike = new ArrayList<Bitmap>();
	
	//sluzi samo za simulaciju albuma, nis korisno, kasnije treba obrisat
	
	private ArrayList<Drawable> album1 = new ArrayList<Drawable>();
	private ArrayList<Drawable> album2 = new ArrayList<Drawable>();
	private ArrayList<Drawable> album3 = new ArrayList<Drawable>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		
		mGalleryId = getIntent().getStringExtra(EXTRA_GALLERY_ID);
		
		mGridView = (GridView)findViewById(R.id.gridview);
		
		new Gallery().execute(mGalleryId, this);  
		
		/*
		 * Ovo zakometirano je za simulaciju tri albuma.
		 * Ovisno o id-u prikazat ce se jedan od njih.
		 * Za simulaciju otkomentirati ovo i zakomentirati prvi redak iznad.
		 * Uz to treba otkomentirati i onaj dio u fragment_galleries.
		 * */
		/*
		album1.add(getResources().getDrawable(R.drawable.ic_launcher));
		album1.add(getResources().getDrawable(R.drawable.search));
		album1.add(getResources().getDrawable(R.drawable.more));

		
		album2.add(getResources().getDrawable(R.drawable.search));
		album2.add(getResources().getDrawable(R.drawable.ic_launcher));
		album2.add(getResources().getDrawable(R.drawable.more));
		
		album3.add(getResources().getDrawable(R.drawable.more));
		album3.add(getResources().getDrawable(R.drawable.ic_launcher));
		album3.add(getResources().getDrawable(R.drawable.search));
		
		if(mGalleryId != null){
			if (mGalleryId.equals("10")) {
				mGridView.setAdapter(new GalleryAdapter(this, album1));
			} else if (mGalleryId.equals("21")) {
				mGridView.setAdapter(new GalleryAdapter(this, album2));
			} else {
				mGridView.setAdapter(new GalleryAdapter(this, album3));
			}
		} 

		mGridView.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	Intent i = new Intent(activity_gallery.this, activity_fullscreen_image.class);
				i.putExtra(activity_fullscreen_image.EXTRA_POST_ID, position);
				startActivity(i);
		    }
		});
		*/
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
					startActivity(i);
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
