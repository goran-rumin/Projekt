package com.ferbook;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketConnectionHandler;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MessageFragment extends Fragment implements Conversation.prenesi, View.OnClickListener, Send.prenesi{

		private static final String ARG_SECTION_NUMBER = "section_number";   //redni broj fragmenta, zbog naslova ActionBara
		static String user_id, name, my_id;
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
			my_id = Vrati_id.vrati(getActivity());
			
			
			listview = (ListView) rootView.findViewById(R.id.list_messages);
			adapter = new SimpleAdapter(getActivity(),
					data,
					R.layout.messages_layout,
					new String[] {"messages_text","messages_time"},
					new int[] { R.id.messages_text, R.id.messages_time});
			listview.setAdapter(adapter);
			new Conversation().execute(my_id, user_id, this);   //povezati podatke sa suceljem
			TextView tv1 = (TextView) rootView.findViewById(R.id.messages_user);
			tv1.setText(name);
			poruka = (EditText) rootView.findViewById(R.id.message_text);
			pokreni_websocket();
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		
		@Override
		public void onPause(){
			data.clear();
			super.onPause();
		}

		@Override
		public void prenesi_conversation(List<String> messages,
				List<String> SenderIds, List<String> timestamps,
				List<Integer> flags, int broj_poruka, String error) {
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
			data.clear();
			HashMap<String,Object> redak;
			for(int i=0;i<broj_poruka;i++){
				redak = new HashMap<String,Object>();
				if(SenderIds.get(i).equals(my_id))
					redak.put("messages_text","Me: "+messages.get(i));
				else
					redak.put("messages_text",name.split("\\ ")[0]+": "+messages.get(i));
				redak.put("messages_time",timestamps.get(i));
				data.add(redak);
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(adapter.getCount() - 1);
		}
		HashMap<String,Object> redak;
		@Override
		public void onClick(View v) {
			String tekst = poruka.getText().toString();
			poruka.setText("");
			Time vrijeme = new Time();
			vrijeme.setToNow();
			new Send().execute(my_id, user_id, tekst, this);
			redak = new HashMap<String,Object>();
			redak.put("messages_text","Me: "+tekst);
			redak.put("messages_time",vrijeme.format("%F %T"));
			data.add(redak);
			if(socket.isConnected()){
				JSONObject objekt = new JSONObject();
				try {
					objekt.put("type", "message");
					objekt.put("sender", my_id);
					objekt.put("recipient", user_id);
					objekt.put("message",tekst);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				socket.sendTextMessage(objekt.toString());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(adapter.getCount() - 1);
		}

		@Override
		public void prenesi_send(String error) {
			if(error!=null)
				Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
		}
		public final WebSocketConnection socket = new WebSocketConnection();
		void pokreni_websocket(){
			final String wsuri = "ws://ferbook.duckdns.org:9000";

			   try {
			      socket.connect(wsuri, new WebSocketConnectionHandler() {

			         @Override
			         public void onOpen() {
			            Log.e("app", "Status: Connected to " + wsuri);
			            JSONObject objekt = new JSONObject();
			            try {
			            	objekt.put("type", "welcome");
							objekt.put("id", Vrati_id.vrati(getActivity()));
						} catch (JSONException e) {
							e.printStackTrace();
						}
			            socket.sendTextMessage(objekt.toString());
			         }

			         @Override
			         public void onTextMessage(String payload) {
			            Log.e("app", "Received: " + payload);
			            String posiljatelj,primatelj,poruka, vrijeme;
			            try {
							JSONObject objekt = new JSONObject(payload);
							primatelj = objekt.getString("recipient");
							posiljatelj = objekt.getString("sender");
							poruka = objekt.getString("message");
							vrijeme = objekt.getJSONObject("msg").getString("timestamp");
							Date datum = new Date(Long.valueOf(vrijeme)*1000);
							String form_vr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(datum);
							if(!posiljatelj.equals(user_id)){
								Toast.makeText(getActivity(), "New messages in other conversations", Toast.LENGTH_SHORT).show();
							}
							else{
								HashMap<String,Object> redak = new HashMap<String,Object>();
								redak.put("messages_text",name.split("\\ ")[0]+": "+poruka);
								redak.put("messages_time",form_vr);
								data.add(redak);
								adapter.notifyDataSetChanged();
								listview.setSelection(adapter.getCount() - 1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
			         }

			         @Override
			         public void onClose(int code, String reason) {
			            Log.e("app", "Connection lost.");
			         }
			      });
			   } catch (WebSocketException e) {

			      Log.e("app", e.toString());
			   }
		}
}

