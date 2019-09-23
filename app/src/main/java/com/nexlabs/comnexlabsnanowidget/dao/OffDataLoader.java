package com.nexlabs.comnexlabsnanowidget.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;


public class OffDataLoader {
	
	private static final String DATA_FILE = "dataset.json";
	
	// base uri is known by the loader 
	// whereas the relative path towards the actual image is known by the data object which is responsible for its imagepath generation
	private static final String ASSETS_FOLDER = "base_assets/";
	
	private TreeMap<String, Crypto> map;
	private ImageLoader imgLoader; 
	
	
	private static OffDataLoader offData = null;
	private OffDataLoader() {
		map = new TreeMap<>();
		
	}
	
	public static OffDataLoader getInstance() {
		if(offData == null) {
			offData = new OffDataLoader();
			offData.map = offData.load();
			offData.imgLoader = ImageLoader.getInstance(ASSETS_FOLDER);
		}
		return offData;
	}
	
	
	private TreeMap<String, Crypto> load() {
		List<Crypto > list = new ArrayList<>();
		
		StringBuffer sb = new StringBuffer();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
			
			String line = null;
			while( (line = reader.readLine()) != null ) {
				sb.append(line);
			}
			
			JSONArray jsonArray = new JSONArray(new String(sb));
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String id = jsonObject.getString( Property.ID.toString());
				String symbol = jsonObject.getString(Property.SYMBOL.toString());
				String name = jsonObject.getString(Property.NAME.toString());
				
				Crypto crypto = new Crypto(id, symbol, name);
				
				map.put( id  , crypto);
			}
			reader.close();			
		}catch(IOException e) {
			e.printStackTrace();
		}catch(JSONException e) {
			e.printStackTrace();
		}
		
		
		return map;
	}

	public Collection<Crypto> getAll(){
		return map.values();
	}
	// id is lowercased name
	public Crypto get(String id) {
		return map.get( id );
	}
	
//	public Crypto get(String ticker, String name) {
//		return map.get( key(ticker, name) );
//	}
	
	public void getImage(String ticker, String name) {
		// TODO
	}
	
	
	
	
	
//	
// 	private static String key(String ticker, String name) {
// 		return ImageSet.getKey(ticker, name);
// 	}
// 
// 	
 	
 	public static void main(String args[]) {
 		OffDataLoader loader = OffDataLoader.getInstance();
 		for(Crypto c : loader.getAll()){
 			System.out.println(c.getId() + " " + c.getSymbol() + " " + c.getName());
 		}
 		
 		Crypto nano = loader.get("nano");
 		System.out.println(nano.getId() + " " + nano.getSymbol() + " " + nano.getName());
 		
 	}
 	

}
