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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * Ova aktivnost sluzi za dodavanje slika u postojeci album
 * */
public class activity_addPicturesToAlbum extends Activity {
	
	final int ACTIVITY_CHOOSE_FILE = 1;
	public static final String EXTRA_GALLERY_ID = "com.ferbook.image_position";
		
	private String mGalleryId;
	private GridView mGridView;
	private Boolean smije_objaviti = true;
	private ArrayList<String> mPicturePaths = new ArrayList<String>();
	private ArrayList<Bitmap> mPictures = new ArrayList<Bitmap>();	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery); //samo nam treba GridView	

		mGalleryId = getIntent().getStringExtra(EXTRA_GALLERY_ID);
		
		mGridView = (GridView)findViewById(R.id.gridview);
	}
	
	
	/*
	 * MENU OPTIONS
	 * */
	@Override
   	public boolean onCreateOptionsMenu(Menu menu) {
       	getMenuInflater().inflate(R.menu.galleries_add_pictures, menu); //button za dodavanje slike i upload
       	return true;
   	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_album: //dodaj novu sliku
				Intent chooseFile;
				Intent intent;
				chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
				chooseFile.setType("image/jpeg");
				intent = Intent.createChooser(chooseFile, "Choose a picture");
				startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
				return true;
				
			case R.id.menu_item_upload:
				
				if (mPicturePaths.size() == 0 && smije_objaviti){
					Toast.makeText(this, "Niste odabrali niti jednu sliku." , Toast.LENGTH_SHORT).show();
				} else if (smije_objaviti){
					smije_objaviti = false;
					Toast.makeText(this, "Starting upload..." , Toast.LENGTH_SHORT).show();
					for (int i = 0; i < mPicturePaths.size(); i++){
						Bitmap slika = BitmapFactory.decodeFile(mPicturePaths.get(i));
						new Upload().execute(Vrati_id.vrati(this), slika, mGalleryId, this); 
					}
					Toast.makeText(this, "Upload finished." , Toast.LENGTH_SHORT).show();
					smije_objaviti=true;
					mPicturePaths.clear();
					return true;
				} else {
					Toast.makeText(this, "Pictures are uploading..." , Toast.LENGTH_SHORT).show();
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
							
							put = getRealPathFromURI(uri); //iz puta cu dobiti Bitmap sliku
							
							mPicturePaths.add(put); //spremam puteve svih slika koje treba dodati
							
							//napravi sample sliku za galeriju, koja je manja od prave slike
							Bitmap myPicture =  decodeSampledBitmapFromResource(put, 100, 100); 
							
							mPictures.add(myPicture); //svi samplovi za prikaz
							mGridView.setAdapter(new GalleryAdapter(this, mPictures));
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
	
	
	/*
	 * Vrati Bitmap iz puta, zeljene velicine
	 * */
	public static Bitmap decodeSampledBitmapFromResource(String put,int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(put, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(put, options);
	}
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
			    // Raw height and width of image
			    final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;
			
			    if (height > reqHeight || width > reqWidth) {
			
			        final int halfHeight = height / 2;
			        final int halfWidth = width / 2;
			
			        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
			        // height and width larger than the requested height and width.
			        while ((halfHeight / inSampleSize) > reqHeight
			                && (halfWidth / inSampleSize) > reqWidth) {
			            inSampleSize *= 2;
			        }
			    }
			
			    return inSampleSize;
	}
	
	
	/*
	 * Put iz URI
	 * */
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
	
	
	/*
	 * ADAPTER
	 * */
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
