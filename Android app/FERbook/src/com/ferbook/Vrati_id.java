package com.ferbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;


//clasa koja vraca id korisnika
public class Vrati_id {

	public static String vrati(Activity ak){
		
		String id=null;
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(ak.openFileInput("id.txt")));
			id = bf.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}
	
}
