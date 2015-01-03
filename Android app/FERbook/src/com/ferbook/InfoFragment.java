package com.ferbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class InfoFragment extends Fragment implements Info.prenesi{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara

		static NavigationDrawerFragment ladica;
		static ArrayList<HashMap<String,?>> data;
		static TextView nema_konv;
		static ProgressBar progres;
		String user_id;
		View rootView;
		
		public static InfoFragment newInstance(int sectionNumber) {
			InfoFragment fragment = new InfoFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public InfoFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			rootView = inflater.inflate(R.layout.info, container,
					false);
			progres = (ProgressBar) rootView.findViewById(R.id.info_progress);
			user_id=getArguments().getString("userid");
			new Info().execute(user_id, this);
			return rootView;
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			/*((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));*/
		}

		@Override
		public void prenesi_info(String username,String name, String lastName, String email, Drawable pic, String error) {
			progres.setVisibility(View.INVISIBLE);
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			TextView usr = (TextView) rootView.findViewById(R.id.info_username);
			TextView nm = (TextView) rootView.findViewById(R.id.info_name);
			TextView lstnm = (TextView) rootView.findViewById(R.id.info_lastname);
			TextView mail = (TextView) rootView.findViewById(R.id.info_email);
			ImageView pict = (ImageView) rootView.findViewById(R.id.info_picture);
			usr.setText("Username: "+username);
			nm.setText("Name: "+name);
			lstnm.setText("Lastname: "+lastName);
			mail.setText("E-mail: "+email);
			pict.setImageDrawable(pic);
		}
}
