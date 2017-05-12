package main.java.suyam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class GetURLContent {
	public static String getData(String strURL) {

		URL url;
		StringBuffer buffer = new StringBuffer();

		try {
			// get URL content
		
			url = new URL(strURL);
			InputStream is = url.openStream();
			int ptr = 0;
			
			while ((ptr = is.read()) != -1) {
			    buffer.append((char)ptr);
			}
			
			System.out.println("Done reading from URL");
			

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();

	}
}