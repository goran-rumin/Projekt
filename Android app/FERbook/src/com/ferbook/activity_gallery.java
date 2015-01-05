package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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
	
	public static final String EXTRA_GALLERY_ID = "com.ferbook.image_position";
	
	private String mGalleryId;
	private ArrayList<Drawable> slike = new ArrayList<Drawable>();
	private ArrayList<Drawable> album = new ArrayList<Drawable>();
	private ArrayList<String> ids = new ArrayList<String>();
	private GridView gridView;
	
	//sluzi samo za simulaciju albuma, nis korisno, kasnije treba obrisat
	private ArrayList<Drawable> album1 = new ArrayList<Drawable>();
	private ArrayList<Drawable> album2 = new ArrayList<Drawable>();
	private ArrayList<Drawable> album3 = new ArrayList<Drawable>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		
		//Iz EXTRA se dohvati Id galerije.
		mGalleryId = getIntent().getStringExtra(EXTRA_GALLERY_ID);
		
		gridView = (GridView)findViewById(R.id.gridview);
		
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
				gridView.setAdapter(new GalleryAdapter(this, album1));
			} else if (mGalleryId.equals("21")) {
				gridView.setAdapter(new GalleryAdapter(this, album2));
			} else {
				gridView.setAdapter(new GalleryAdapter(this, album3));
			}
		} 
		
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	Intent i = new Intent(getApplicationContext(), activity_fullscreen_image.class);
		    	//dodajemo EXTRA jer tako dajemo do znanja koju sliku treba prikazati
				i.putExtra(activity_fullscreen_image.EXTRA_POST_ID, position);
				startActivity(i);
		    }
		});
		*/
	}
	
	@Override
	public void prenesi_gallery(final List<String> postIds, List<Drawable> slike, int broj_slika, String error) {
		
		if (error == null) {
			for (int i = 0; i < broj_slika; i++) {
				album.add(slike.get(i));
				ids.add(postIds.get(i));
				
				//postavi adapter
				gridView.setAdapter(new GalleryAdapter(this, slike));
				
				gridView.setOnItemClickListener(new OnItemClickListener() 
				{
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {
				    	Intent i = new Intent(getApplicationContext(), activity_fullscreen_image.class);
				    	//Id posta u kojem je slika se predaje kao EXTRA
						i.putExtra(activity_fullscreen_image.EXTRA_POST_ID, postIds.get(position));
						startActivity(i);
				    }
				});
			}
		} else {
			Toast.makeText(this, "Greska, pokusajte ponovo.", Toast.LENGTH_SHORT).show();
		}
	}
	
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
