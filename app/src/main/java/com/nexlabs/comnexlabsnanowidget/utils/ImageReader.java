package com.nexlabs.comnexlabsnanowidget.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nexlabs.comnexlabsnanowidget.AssetsUtils;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ImageReader {

    public final static String IMAGES_DIR = "images";
    public final static String SMALL_IMAGES_REL_PATH = "images/small";

    public static String getImageFileStoragePath(Context context, String cryptoId, ImgType type){
        File internalDir = context.getFilesDir();
        File smallImagesDir = new File(internalDir, SMALL_IMAGES_REL_PATH);
        File imageFile = new File(smallImagesDir, cryptoId + ".png");
        return imageFile.getPath();
    }

    public static String getImageDirStoragePath(Context context, ImgType type){
        File internalDir = context.getFilesDir();
        File smallImagesDir = new File(internalDir, SMALL_IMAGES_REL_PATH);
        return smallImagesDir.getPath();
    }
    public static boolean imageFilePathExists(Context context, String cryptoId, ImgType type){
        File internalDir = context.getFilesDir();
        File smallImagesDir = new File(internalDir, SMALL_IMAGES_REL_PATH);
        File imageFile = new File(smallImagesDir, cryptoId + ".png");
        return imageFile.exists();
    }
    public static boolean imageDirPathExists(Context context, ImgType type){
        File internalDir = context.getFilesDir();
        File smallImagesDir = new File(internalDir, SMALL_IMAGES_REL_PATH);
        return smallImagesDir.exists();
    }


    public static Bitmap readImageFromStorage(Context context, String  cryptoId, ImgType type){
        Bitmap image = null;
        File internalDir = context.getFilesDir();
        File smallImagesDir = new File(internalDir, SMALL_IMAGES_REL_PATH);
        File imageFile = new File(smallImagesDir, cryptoId + ".png");

        image = BitmapFactory.decodeFile(imageFile.getPath());

        return image;
    }

    public static Bitmap readImage(Context context, String  cryptoId, ImgType type){
        Bitmap image = null;
        try{
            image = BitmapFactory.decodeStream( context.getAssets()
                    .open(AssetsUtils.IMAGE_ASSETS_PATH +
                            cryptoId  +"/" +
                            type.toString() + ".png") );
        }catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }
    public static Bitmap readImage(Context context, Crypto crypto, ImgType type){
        Bitmap image = null;
        try{
            image = BitmapFactory.decodeStream( context.getAssets()
                    .open(AssetsUtils.IMAGE_ASSETS_PATH +
                            crypto.getSymbol() +"/" +
                            type.toString() + ".png") );
        }catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }

}
