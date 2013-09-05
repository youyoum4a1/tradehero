package com.tradehero.th.cache;

import android.content.Context;
import java.io.File;

@Deprecated
public class FileCache
{

    private File cacheDir;
    private final static String DIRECTORY = "TH";

    public FileCache(Context context)
    {
        //Find the dir to save cached images
        if (android.os
                .Environment
                .getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED))
        {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), DIRECTORY);
        }
        else
        {
            cacheDir = context.getCacheDir();
        }

        if (!cacheDir.exists())
        {
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url)
    {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
    }

    public void clear()
    {
        File[] files = cacheDir.listFiles();
        if (files == null)
        {
            return;
        }
        for (File f : files)
            f.delete();
    }
}