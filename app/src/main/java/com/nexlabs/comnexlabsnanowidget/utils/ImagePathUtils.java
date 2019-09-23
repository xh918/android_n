package com.nexlabs.comnexlabsnanowidget.utils;

import com.nexlabs.comnexlabsnanowidget.AssetsUtils;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;

public class ImagePathUtils {

    public static String getAssetLogoPath(Crypto crypto, ImgType type){
        return  AssetsUtils.IMAGE_ASSETS_PATH +
                crypto.getId()  +"/" +
                type.toString() + ".png";
    }


}
