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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private String userId;
	
	private GridView mGridView;
	private View view;
	private TextView noAlbumMessage;
	
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
		setHasOptionsMenu(true);
		
		mGridView = (GridView) view.findViewById(R.id.gridview);
		noAlbumMessage = (TextView) view.findViewById(R.id.noAlbumMessage);
		
		userId = Vrati_id.vrati(getActivity());

		new Galleries().execute(userId, this);
	
		/*
		 * Ovo zakomentirano je simulacija albuma da se moze vidjeti kako ce to izgledati.
		 * Simulacija tri albuma koji imaju id: 10, 21, 5.
		 * Treba ovo otkomentirati i zakomentirati redak iznad: new Galleries... 
		 * */
		/*
		mNaslovnice.add(getResources().getDrawable(R.drawable.ic_launcher));
		mNaslovnice.add(getResources().getDrawable(R.drawable.search));
		mNaslovnice.add(getResources().getDrawable(R.drawable.more));
		
		mNazivi.add("Album 1");
		mNazivi.add("Album 2");
		mNazivi.add("Album 2");
		
		mIds.add("10");
		mIds.add("5");
		mIds.add("20");
		
		mGridView.setAdapter(new GalleryImageAdapter(view.getContext(), mNaslovnice, mIds));  
		*/
		
		mGridView.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	Log.d("denis", mGridView.getItemAtPosition(position).toString());
		    	Intent i = new Intent(getActivity(), activity_gallery.class);
		    	String galleryId = (String) (mGridView.getItemAtPosition(position));
				i.putExtra(activity_gallery.EXTRA_GALLERY_ID, galleryId);
				startActivity(i);
		    }
		});
		
		
		return view;
	}
	
	/*
	 * MENU
	 * */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.add_gallery, menu); //samo button za novu galeriju
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_album:
				Intent i = new Intent(getActivity(), activity_newAlbum.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * GALLERIES
	 * Prikazi sve galerije
	 * */
	@Override
	public void prenesi_getlikes(List<String> albumIds, List<String> names, List<Drawable> naslovnice, 
			int broj_galerija, String error) {
		
		if (error == null) {
			for (int i = 0; i < broj_galerija; i++) { 
				mNaslovnice.add(naslovnice.get(i));
				mNazivi.add(names.get(i));
				mIds.add(albumIds.get(i));
			}
			
			mGridView.setAdapter(new GalleryImageAdapter(view.getContext(), mNaslovnice, mIds, mNazivi));
		} else {
			//Ako ne postoji niti jedan album, postavi poruku.
			mGridView.setVisibility(View.INVISIBLE);
			noAlbumMessage.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
	
	//Osvjezi galeriju kada se korisnik vrati iz activity_newAlbum
	@Override
    public void onResume(){
    	super.onResume();
    	//new Galleries().execute(userId, this);
    }
}
