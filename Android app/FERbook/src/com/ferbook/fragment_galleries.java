package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;


/*
 * Ovaj fragment prikazuje albume korisnika.
 * Svaki album predstavljen je naslovnom slikom i naslovom albuma.
 * Klikom na album pokrece se nova aktivnost koja prikazuje slike albuma.
 * */
public class fragment_galleries extends Fragment implements Galleries.prenesi {
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	private ArrayList<Drawable> mNaslovnice = new ArrayList<Drawable>();
	private ArrayList<String> mNazivi = new ArrayList<String>();
	private ArrayList<String> mIds = new ArrayList<String>();
	private ArrayList<Album> mAlbums = new ArrayList<Album>();
	
	private GridView gridView;
	private View view;
	private TextView noAlbumMessage;
	
	/*
	 * U fragment se kao argument salje sectionNumber 
	 * zbog odredivanja naslova u ActionBaru.
	 * */
	public static fragment_galleries newInstance(int sectionNumber) {
		fragment_galleries fragment = new fragment_galleries();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.gallery, container, false);
			
		gridView = (GridView) view.findViewById(R.id.gridview);
		
		noAlbumMessage = (TextView) view.findViewById(R.id.noAlbumMessage);
		
		String userId = Vrati_id.vrati(getActivity());

		new Galleries().execute(userId, this);
		
		/*
		 * Ovo zakomentirano je simulacija albuma da se moze vidjeti kako ce to izgledati.
		 * Simulacija tri albuma koji imaju id: 10, 21, 5.
		 * Treba ovo otkomentirati i zakomentirati redak iznad: new Galleries... 
		 * */
		/*
		Album album;
		album = new Album("10", "Prvi album", getResources().getDrawable(R.drawable.ic_launcher));
		mAlbums.add(album);
		album = new Album("21", "Drugi album", getResources().getDrawable(R.drawable.search));
		mAlbums.add(album);
		album = new Album("5", "Treci album", getResources().getDrawable(R.drawable.more));
		mAlbums.add(album);
		
		for (int i = 0; i < 3; i++) {
			mNaslovnice.add(mAlbums.get(i).getAlbumMainPicture());
			mNazivi.add(mAlbums.get(i).getAlbumName());
			mIds.add(mAlbums.get(i).getAlbumId());
		}
		
		gridView.setAdapter(new GalleryImageAdapter(view.getContext(), mNaslovnice, mIds));  
		
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	Intent i = new Intent(getActivity(), activity_gallery.class);
		    	String galleryId = (String) (gridView.getItemAtPosition(position));
				i.putExtra(activity_gallery.EXTRA_GALLERY_ID, galleryId);
				startActivity(i);
		    }
		});
		*/
		
		return view;
	}
	
	@Override
	public void prenesi_getlikes(List<String> albumIds, List<String> names, List<Drawable> naslovnice, 
			int broj_galerija, String error) {
		

		if (error == null) { //Ako postoje albumi
			for (int i = 0; i < broj_galerija; i++) {
				Album album = new Album(albumIds.get(i), names.get(i), naslovnice.get(i));
				mAlbums.add(album); 
				mNaslovnice.add(album.mMainPicture);
				mNazivi.add(album.mName);
				mIds.add(album.mId);
			}
			
			//Postavi adapter
			gridView.setAdapter(new GalleryImageAdapter(view.getContext(), mNaslovnice, mIds));
			
			gridView.setOnItemClickListener(new OnItemClickListener(){
			    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
			
			    	Intent i = new Intent(getActivity(), activity_gallery.class);
			    	//Id galerije se salje kao EXTRA.
			    	String galleryId = (String) (gridView.getItemAtPosition(position));
					i.putExtra(activity_gallery.EXTRA_GALLERY_ID, galleryId);
					startActivity(i);
			    }
			});
			
		} else {
			//Ako ne postoji niti jedan album, postavi poruku.
			gridView.setVisibility(View.INVISIBLE);
			noAlbumMessage.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
	
	private class Album {
		private String mId;
		private String mName;
		private Drawable mMainPicture;
		
		public Album(String albumId, String albumName, Drawable albumMainPicture) {
			mId = albumId;
			mName = albumName;
			mMainPicture = albumMainPicture;
		}
		
		/*Geteri i seteri*/
		public String getAlbumId() {
			return mId;
		}
		
		public String getAlbumName() {
			return mName;
		}
		
		public Drawable getAlbumMainPicture() {
			return mMainPicture;
		}		
	}
}
