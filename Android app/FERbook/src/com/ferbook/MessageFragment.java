package com.ferbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MessageFragment extends Fragment implements Conversation.prenesi, View.OnClickListener{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara
		static String user_id, name;
		static ArrayList<HashMap<String,?>> data;
		SimpleAdapter adapter; //potrebno za osvjezavanje poruka nakon slanja bez dohvacanja
		EditText poruka;   //potrebno za dohvat teksta
		ListView listview;
		
		public static MessageFragment newInstance(int sectionNumber, String userId, String name_pr) {
			MessageFragment fragment = new MessageFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			user_id=userId;
			name=name_pr;
			data = new ArrayList<HashMap<String,?>>();
			
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("messages_text","Test poruka");
			redak.put("messages_time","14.11.2014. 22:00");
			data.add(redak);
			redak = new HashMap<String,Object>();
			redak.put("messages_text","Test poruka2");
			redak.put("messages_time","14.11.2014. 22:00");
			data.add(redak);
			redak = new HashMap<String,Object>();
			redak.put("messages_text","Test poruka2");
			redak.put("messages_time","14.11.2014. 22:00");
			data.add(redak);
			redak = new HashMap<String,Object>();
			redak.put("messages_text","Test poruka2");
			redak.put("messages_time","14.11.2014. 22:00");
			data.add(redak);
			return fragment;
		}

		public MessageFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.messages, container,
					false);
			Button send = (Button) rootView.findViewById(R.id.send);
			send.setOnClickListener(this);
			String my_id = null;
			try {
				BufferedReader bf = new BufferedReader(new InputStreamReader(getActivity().openFileInput("id.txt")));
				my_id = bf.readLine();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			listview = (ListView) rootView.findViewById(R.id.list_messages);
			adapter = new SimpleAdapter(getActivity(),
					data,
					R.layout.messages_layout,
					new String[] {"messages_text","messages_time"},
					new int[] { R.id.messages_text, R.id.messages_time});
			listview.setAdapter(adapter);
			new Conversation().execute(my_id, user_id, 20, this);   //povezati podatke sa suceljem
			TextView tv1 = (TextView) rootView.findViewById(R.id.messages_user);
			tv1.setText(name);
			poruka = (EditText) rootView.findViewById(R.id.message_text);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}

		@Override
		public void prenesi_conversation(String message, String SenderId,
				String timestamp, int flag, String error) {
			//vracanje podataka
		}

		@Override
		public void onClick(View v) {
			String tekst = poruka.getText().toString();
			//poslati tekst na server
			poruka.setText("");
			Time vrijeme = new Time();
			vrijeme.setToNow();
			HashMap<String,Object> redak = new HashMap<String,Object>();
			redak.put("messages_text",tekst);
			redak.put("messages_time",vrijeme.format("%d.%m.%y. %R"));
			data.add(redak);
			adapter.notifyDataSetChanged();
			listview.setSelection(adapter.getCount() - 1);
		}
}
