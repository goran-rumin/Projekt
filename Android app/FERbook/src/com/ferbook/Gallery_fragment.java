package com.ferbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class Gallery_fragment extends Fragment {
	
	private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara
	

	public static Gallery_fragment newInstance(int sectionNumber) {
		Gallery_fragment fragment = new Gallery_fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	public Gallery_fragment() {
	}
	
	//private List<Integer> mThumbIds = new ArrayList<Integer>();

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gallery, container, false);
		GridView gridView = (GridView) view.findViewById(R.id.gridview);	
		
		/*
		mThumbIds.add(R.drawable.sample_2);
		mThumbIds.add(R.drawable.sample_3);
		mThumbIds.add(R.drawable.sample_4);
		mThumbIds.add(R.drawable.sample_5);
		mThumbIds.add(R.drawable.sample_6);
		mThumbIds.add(R.drawable.sample_7);
		*/
		
		//predaje se lista slika
		//gridView.setAdapter(new ImageAdapter(view.getContext(), slike));   
		
		return view;
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
