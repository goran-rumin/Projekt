package com.ferbook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


//clasa koja vraća id korisnika
public class Vrati_id {

	public String vrati(){
		
		String id=null;
		try {
		BufferedReader in
		   = new BufferedReader(new FileReader("id.txt"));
			
			id = in.readLine();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return id;
	}
	
}
