package com.ferbook;


import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


public class activity_fullscreen_image extends Activity implements Image.prenesi {
	
	public static final String EXTRA_POST_ID = "com.ferbook.image_position";
	
	//za simulaciju, nis korisno, treba obrisat poslije
	private ArrayList<Drawable> slike = new ArrayList<Drawable>();
	
	private ImageView imageView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.activity_fullscreenpicture);
        
        imageView = (ImageView)findViewById(R.id.imgDisplay); 
        
        String postId =  (String)getIntent().getStringExtra(EXTRA_POST_ID);
        
        new Image().execute(postId);
        
        /*
         * Za simulaciju.
         * Otkomentiraj ovo i zakomentiraj liniju iznad.
         * Uz to otkomentiraj dio u fragment_galleries i activity_gallery
         * */
        /*
        slike.add(getResources().getDrawable(R.drawable.ic_launcher));
		slike.add(getResources().getDrawable(R.drawable.search));
		slike.add(getResources().getDrawable(R.drawable.more));
		//za primjer sam stavio nek prikaze prvu sliku uvijek
		imageView.setImageDrawable(slike.get(0));
		*/

        
    }
    
    @Override
    public void prenesi_image(Drawable slika, String error) {
    	if (error == null) {
    		imageView.setImageDrawable(slika);
    	} else {
    		Toast.makeText(this, "Greska, pokusajte ponovo.", Toast.LENGTH_SHORT).show();
    	}
    }
}
