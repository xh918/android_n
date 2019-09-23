package com.nexlabs.comnexlabsnanowidget.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageLoader {
	
	
	private static ImageLoader imgLoader = null;
	private String imageAssetsPath;
	
	private ImageLoader() {
		
	}
	
	private ImageLoader(String path) {
		this.imageAssetsPath = path;
	}
	
	// usable only by OffDataLoader which knows the exact assetsPath
	public static ImageLoader getInstance(String imageAssetsPath) {
		if(imgLoader == null) {
			imgLoader = new ImageLoader(imageAssetsPath);
		}
		return imgLoader;
	}
	
	private String makeImageFilePath(String relativeResourcePath) {
		return imageAssetsPath + relativeResourcePath;
	}
	
	
	
	
	public Bitmap decodeImageFromPath(String relativeResourcePath) {
		String absPath = makeImageFilePath(relativeResourcePath);
		// TODO bitmap fetch methods
		return BitmapFactory.decodeFile(absPath);
	}
	

}
