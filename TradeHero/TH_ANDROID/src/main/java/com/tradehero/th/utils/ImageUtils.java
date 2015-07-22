package com.tradehero.th.utils;

import android.content.Context;
import android.os.Environment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.tradehero.th.R;

public class ImageUtils
{
    public static String getImageStoragePath(Context context){
        String dir = "/th";
        String path = getDefaultStoragePath(context) + dir;
        return path;
    }

    public static String getDefaultStoragePath(Context context){
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && Environment.getExternalStorageDirectory()
                .canWrite())
        {
            return Environment.getExternalStorageDirectory().getPath();
        }
        else
        {
            return context.getFilesDir().getPath();
        }
    }

    public static DisplayImageOptions getAvatarImageLoaderOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.superman_facebook)
                .showImageForEmptyUri(R.drawable.superman_facebook)
                .showImageOnFail(R.drawable.superman_facebook)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        return options;
    }

}
