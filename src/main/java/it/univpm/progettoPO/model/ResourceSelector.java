package it.univpm.progettoPO.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class ResourceSelector {
	
	private String url;
	private String array_pos;	// es "result.resources"
	
	public ResourceSelector(String url, String array_pos) {
		this.url = url;
		this.array_pos = array_pos;
	}
	
	public String getUrlByFormat(String type) {
		
		String outString = "";
		
		//controllo la presenza del file, se c'è lo cancello
		try {
			File fp = new File("t1.csv");
			fp.delete();
		}catch(Exception e) {
		//se non c'è non faccio nulla
		}
		
		try {
			
			URL myurl = new URL(this.url);
			URLConnection openConnection = myurl.openConnection();
			openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
			InputStream in = openConnection.getInputStream();
			
			 String data = "";
			 String line = "";
			 try {
			   InputStreamReader inR = new InputStreamReader( in );
			   BufferedReader buf = new BufferedReader( inR );
			  
			   while ( ( line = buf.readLine() ) != null ) {
				   data+= line;
				   System.out.println( line );
			   }
			 } finally {
			   in.close();
			 }

			JSONObject obj = (JSONObject) JSONValue.parseWithException(data);
			String[] deep_level = this.array_pos.split("-");
			ArrayList<JSONObject> deep = new ArrayList<JSONObject>();
			
			for(int i = 0; i<deep_level.length -1;i++)
				deep.add((JSONObject) (obj.get(deep_level[i])));
			JSONArray objA = (JSONArray) deep.get(deep.size()-1).get(deep_level[deep_level.length-1]);
			
			for(Object o: objA){
			    if ( o instanceof JSONObject ) {
			        JSONObject o1 = (JSONObject)o; 
			        String format = (String)o1.get("format");
			        String urlD = (String)o1.get("url");
			        System.out.println(format + " | " + urlD);
			        if(format.contains(type)) {
			        	outString = urlD;
			        }
			    }
			}
			System.out.println( "OK" );
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outString;
	}
	
	public void download(String url, String fileName) throws Exception {
	    try (InputStream in = URI.create(url).toURL().openStream()) {
	        Files.copy(in, Paths.get(fileName));
	    }
	}
	
}

