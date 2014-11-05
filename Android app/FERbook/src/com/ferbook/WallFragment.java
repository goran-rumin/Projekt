package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WallFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		static ArrayList<HashMap<String,?>> data;
		
		public static WallFragment newInstance(int sectionNumber) {
			WallFragment fragment = new WallFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			
			data = new ArrayList<HashMap<String,?>>();
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("news_item_pimage",R.drawable.ic_launcher);
			redak.put("news_item_ptext","Korisnik0");
			redak.put("news_item_text","Objava 0");
			data.add(redak);
			redak = new HashMap<String,Object>();
			redak.put("news_item_pimage",R.drawable.ic_launcher);
			redak.put("news_item_ptext","Korisnik1");
			redak.put("news_item_text","Objava 1");
			data.add(redak);
			return fragment;
		}

		public WallFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.wall, container,
					false);
			ListView listview = (ListView) rootView.findViewById(R.id.list_wall);
			SimpleAdapter adapter = new SimpleAdapter(getActivity(),
					data,
					R.layout.news_layout,
					new String[] {"news_item_pimage","news_item_ptext","news_item_text"},
					new int[] { R.id.news_item_pimage, R.id.news_item_ptext,R.id.news_item_text});
			listview.setAdapter(adapter);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
}
