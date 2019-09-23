package com.nexlabs.comnexlabsnanowidget.datapackage;



import android.util.JsonWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;


public class ImageSet{
	
	
	private static final String missingSmallURL = "https://assets.coingecko.com/coins/images/5720/small/F1nTlw9I_400x400.jpg?1535438668";
			
	private static final String missingThumbURL = "https://assets.coingecko.com/coins/images/5720/thumb/F1nTlw9I_400x400.jpg?1535438668";
	
	private static final String missingLargeURL = "https://assets.coingecko.com/coins/images/5720/large/F1nTlw9I_400x400.jpg?1535438668";
	
	
	private static HashMap<String, String> missingMap = new HashMap<>();
	
	static {
		missingMap.put(ImgType.SMALL.toString(), missingSmallURL);
		missingMap.put(ImgType.THUMB.toString(), missingThumbURL);
		missingMap.put(ImgType.LARGE.toString(), missingLargeURL);
	}
	
	String small;
	String thumb;
	String large;

	
	
	
	public ImageSet(String small, String thumb, String large) {
		super();
		this.small = small;
		this.thumb = thumb;
		this.large = large;
	}

	public String toJSONString() {
		try {


			Appendable app = new StringBuffer("");
			StringWriter sw = new StringWriter();
			JsonWriter writer = new JsonWriter(sw);

			writer.beginObject().name("small").value(small)
					.name("thumb").value(thumb)
					.name("large").value(large).endObject();
			System.out.println(app);

			return new String((StringBuffer) app);
		}catch (IOException e ){

		}
		return null;
	}


	
//				// name is case sensitive
//	public static String getKey(String ticker, String name) {
//		return ticker + name;
//	}

	public static String getKey(String id) {
		return id;
	}


	
	
		
}
