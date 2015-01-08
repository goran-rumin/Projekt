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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * Ova aktivnost sluzi za odabir novih slika i kreiranje albuma
 * koji ce se uploadati
 * */

public class activity_newAlbum extends Activity {
		
	private String mGalleryId;
	private ArrayList<Bitmap> mPictures = new ArrayList<Bitmap>();
	private GridView gridView;
	private Boolean smije_objaviti = true;
	private ArrayList<Drawable> album1 = new ArrayList<Drawable>();
	private EditText editText_imeAlbuma;
	
	final int ACTIVITY_CHOOSE_FILE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_album);	

		gridView = (GridView)findViewById(R.id.gridview);
		editText_imeAlbuma = (EditText)findViewById(R.id.album_title);	
	}
	
	@Override
	   	public boolean onCreateOptionsMenu(Menu menu) {
	       	// Inflate the menu; this adds items to the action bar if it is present.
	       	getMenuInflater().inflate(R.menu.galleries_add_pictures, menu);
	       	return true;
	   	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_album:
				Intent chooseFile;
				Intent intent;
				chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
				chooseFile.setType("image/jpeg");
				intent = Intent.createChooser(chooseFile, "Choose a picture");
				startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
				
				return true;
			case R.id.menu_item_upload:
				if (mPictures.size() > 0) {
					if (editText_imeAlbuma.getText().toString() != "") {
						smije_objaviti=false;
						Toast.makeText(this, "Starting upload..." , Toast.LENGTH_SHORT).show();
						
						//naredba za upload
						
						Toast.makeText(this, "Upload finished." , Toast.LENGTH_SHORT).show();
						smije_objaviti=true;
						mPictures.clear();
						return true;
					} else {
						Toast.makeText(this, "Niste upisali ime albuma." , Toast.LENGTH_SHORT).show();
					}		
				} else {
					Toast.makeText(this, "Niste odabrali niti jednu sliku." , Toast.LENGTH_SHORT).show();
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

							mPictures.add(slika);
							gridView.setAdapter(new GalleryAdapter(this, mPictures));
							
							Toast.makeText(this, "Pritisnite upload ili dodajte novu sliku.", Toast.LENGTH_SHORT).show();
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
	
	
	
	public class GalleryAdapter extends BaseAdapter{
		
		private List<Bitmap> pictures = new ArrayList<Bitmap>();
        private LayoutInflater inflater;

        public GalleryAdapter(Context c, List<Bitmap> slike)
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
            picture.setImageBitmap(pictures.get(position));

            return view;
        }
	}
}
