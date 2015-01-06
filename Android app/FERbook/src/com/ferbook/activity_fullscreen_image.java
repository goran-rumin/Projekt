package com.ferbook;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * Ovak aktivnost prikazuje sliku kao fullscreen
 * */

public class activity_fullscreen_image extends Activity implements Image.prenesi, GetLikes.prenesi, Like.prenesi {
	
	public static final String EXTRA_POST_ID = "com.ferbook.image_position";
	
	//za simulaciju, nis korisno, treba obrisat poslije
	private ArrayList<Drawable> slike = new ArrayList<Drawable>();
	
	private ImageView imageView;
	private Button mButtonLike;
	private String postId;
	private Boolean mIsLiked;
	private int mNumberOfLikes;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.activity_fullscreenpicture);
        
        imageView = (ImageView)findViewById(R.id.imgDisplay); 
        mButtonLike = (Button)findViewById(R.id.btnLike);
        
        
        postId =  (String)getIntent().getStringExtra(EXTRA_POST_ID);
        
        
        //new Image().execute(postId);
        //new Like().execute(Vrati_id.vrati(this), postId, this, R.layout.activity_fullscreenpicture); 
        //new GetLikes().execute(postId, this);
        
        /*
         * Za simulaciju.
         * Otkomentiraj ovo i zakomentiraj liniju iznad.
         * Uz to otkomentiraj dio u fragment_galleries i activity_gallery
         * */
        /*
        mNumberOfLikes = 50;
        mButtonLike.setText("Like (" + mNumberOfLikes + ")");
        mIsLiked = false;
        slike.add(getResources().getDrawable(R.drawable.ic_launcher));
		slike.add(getResources().getDrawable(R.drawable.search));
		slike.add(getResources().getDrawable(R.drawable.more));
		//za primjer sam stavio nek prikaze prvu sliku uvijek
		imageView.setImageDrawable(slike.get(2));
		*/
		
		mButtonLike.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				//Dodaj sto treba dodati
			}
		});

        
    }
    
    @Override
    public void prenesi_image(Drawable slika, String error) {
    	if (error == null) { //Ako sve ok, prikazi sliku
    		imageView.setImageDrawable(slika);
    	} else {
    		Toast.makeText(this, "Greska, pokusajte ponovo.", Toast.LENGTH_SHORT).show();
    	}
    }
    

    /*
     * GetLikes
     * Koristim za dohvacanje broja likeova
     * */
    @Override
    public void prenesi_getlikes(List<String> likeIds, List<String> timestamps, List<String> userIds, 
    		List<String> names, List<String> lastNames, List<Drawable> pictures, List<String> usernames, List<String> emails, int broj_likeova, String error) {
    	if (error == null) {
    		mNumberOfLikes = broj_likeova;
    	} else {
    		Toast.makeText(this, "Greska kod likeova1", Toast.LENGTH_SHORT).show();
    		mButtonLike.setText("Like (" + "-1" + ")");
    	}
    	
    }
    
    /*
     * Like
     * Provjera je li korisnik lajkao
     * */
    @Override
    public void prenesi_like(String action, String error, View v) {
    	if (error == null) {
    		if (action.equals("like")){
    			mButtonLike.setText("Liked (" + mNumberOfLikes + 1 + ")");
    		} else {
    			mButtonLike.setText("Like (" + mNumberOfLikes + ")");
    		}
    	} else {
    		Toast.makeText(this, "Greska kod likeova2", Toast.LENGTH_SHORT).show();
    	}
    }
}
