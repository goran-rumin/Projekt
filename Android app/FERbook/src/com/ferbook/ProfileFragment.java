package com.ferbook;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import android.support.v4.app.Fragment;
import android.widget.Toast;

public class ProfileFragment extends Fragment{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		private FragmentTabHost host;
		
		public static ProfileFragment newInstance(int sectionNumber) {
			ProfileFragment fragment = new ProfileFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			
			return fragment;
		}

		public ProfileFragment() {
		}

		@SuppressLint("NewApi")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			/*View rootView = inflater.inflate(R.layout.profile_tabovi, container,
					false);*/
			
			host = new FragmentTabHost(getActivity());
			host.setup(getActivity().getBaseContext(), getChildFragmentManager(), R.id.tabcontent);

			host.addTab(host.newTabSpec("wall").setIndicator("Wall"),
	                WallFragment.class, null);
			host.addTab(host.newTabSpec("info").setIndicator("Info"),
	                InfoFragment.class, null);
			
			host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.pozadina_taba);
			TextView naslov = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
			naslov.setTextColor(Color.WHITE);
			host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.pozadina_taba);
			naslov = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
			naslov.setTextColor(Color.WHITE);
			return host;
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
}
