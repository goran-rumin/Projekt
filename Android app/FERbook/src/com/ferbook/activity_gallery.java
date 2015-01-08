package com.ferbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
	private ArrayList<Drawable> slike = new ArrayList<Drawable>();
	private ArrayList<Drawable> album = new ArrayList<Drawable>();
	private ArrayList<String> ids = new ArrayList<String>();
	private GridView gridView;
	private Boolean smije_objaviti = true;
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
		*/
		
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	Intent i = new Intent(activity_gallery.this, activity_fullscreen_image.class);
				i.putExtra(activity_fullscreen_image.EXTRA_POST_ID, position);
				startActivity(i);
		    }
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.galleries_add_pictures, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_album: //korisnik zeli dodati slike u album
				if(!smije_objaviti)
					Toast.makeText(this, "Picture is uploading. Please wait...", Toast.LENGTH_SHORT).show();
				else{
					Intent chooseFile;
					Intent intent;
					chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
					chooseFile.setType("image/jpeg");
					intent = Intent.createChooser(chooseFile, "Choose a picture");
					startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
					
					return true;
				}
			case R.id.menu_item_upload:
				if (nove_slike.size() == 0){
					Toast.makeText(this, "Niste odabrali niti jednu sliku." , Toast.LENGTH_SHORT).show();
				} else {
					smije_objaviti=false;
					Toast.makeText(this, "Starting upload..." , Toast.LENGTH_SHORT).show();
					for (int i = 0; i < nove_slike.size(); i++){
						new Upload().execute(Vrati_id.vrati(this), nove_slike.get(i), mGalleryId, this); 
					}
					Toast.makeText(this, "Upload finished." , Toast.LENGTH_SHORT).show();
					smije_objaviti=true;
					nove_slike.clear();
					new Gallery().execute(mGalleryId, this);  
					return true;
				}
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case ACTIVITY_CHOOSE_FILE:
				if (resultCode == Activity.RESULT_OK){
					Uri uri = data.getData();
					String put = "";
					Bitmap slika = null;
					try {
						if(!uri.getPath().endsWith(".jpg") && !uri.getPath().endsWith(".jpeg")){
							put = getRealPathFromURI(uri);
						}
						if(!put.endsWith(".jpg") && !put.endsWith(".jpeg")){
							Toast.makeText(this, "Supported format is JPG", Toast.LENGTH_SHORT).show();
							smije_objaviti=true;
							break;
						}
						File file = new File(put);
						float velicina = file.length();
						if(velicina<1024*2014){
							slika = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
							nove_slike.add(slika);
							Toast.makeText(this, "Broj novih slika koje ce se dodati: " + nove_slike.size(), Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(this, "Picture is too large", Toast.LENGTH_SHORT).show();
							smije_objaviti=true;
						}
					} catch (FileNotFoundException e) {
						Toast.makeText(this, "File name error. Try using gallery for selection", Toast.LENGTH_SHORT).show();
						smije_objaviti=true;
					} catch (IOException e) {
						Toast.makeText(this, "Supported format is JPG", Toast.LENGTH_SHORT).show();
						smije_objaviti=true;
					} catch (Exception e) {
						Toast.makeText(this, "Supported format is JPG", Toast.LENGTH_SHORT).show();
						smije_objaviti=true;
					}
				}
				
				break;
			default:
				Toast.makeText(this, "File error", Toast.LENGTH_SHORT).show();
		}
	}
	
	public String getRealPathFromURI(Uri contentUri) throws Exception{

        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query( contentUri,
                        proj,
                        null,   
                        null,    
                        null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
	}
	
	@Override
	public void prenesi_gallery(final List<String> postIds, List<Drawable> slike, int broj_slika, String error) {
		
		if (error == null) {
			for (int i = 0; i < broj_slika; i++) {
				album.add(slike.get(i));
				ids.add(postIds.get(i));
				
				gridView.setAdapter(new GalleryAdapter(this, slike));
				
				gridView.setOnItemClickListener(new OnItemClickListener() 
				{
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {
				    	Intent i = new Intent(getApplicationContext(), activity_fullscreen_image.class);
				    	
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
