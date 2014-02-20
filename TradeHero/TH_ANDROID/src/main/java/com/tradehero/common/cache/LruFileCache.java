package com.tradehero.common.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.squareup.picasso.LruCache;
import com.tradehero.th.base.Application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 9/11/13 Time: 7:38 PM To change this template use File | Settings | File Templates. */
public class LruFileCache extends LruCache
{
    public static final String DEFAULT_DIR_NAME = LruFileCache.class.getPackage().getName();
    public static final int DEFAULT_MAX_FILE_SIZE = 200;
    private static MessageDigest md5;
    private static MessageDigest getMd5()
    {
        if (md5 == null)
        {
            try
            {
                md5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new IllegalArgumentException(e);
            }
        }
        return md5;
    }

    private Context context;
    private String dirName;
    private File cacheDir;
    private int maxFileSize;
    private int size = 0;
    private Object dirLock = new Object();
    /**
     * Key is the path, value is the cache key
     */
    final LinkedHashMap<String, String> map;

    //<editor-fold desc="Constructors">
    public LruFileCache(Context context)
    {
        super(context);
        this.maxFileSize = DEFAULT_MAX_FILE_SIZE;
        this.map = new LinkedHashMap<String, String>(0, 0.75f, true);
        initDir(context, DEFAULT_DIR_NAME);
    }

    public LruFileCache(int maxMemSize, int maxFilesize)
    {
        super(maxMemSize);
        this.maxFileSize = maxFileSize;
        this.map = new LinkedHashMap<String, String>(0, 0.75f, true);
        initDir(Application.context(), DEFAULT_DIR_NAME);
    }
    //</editor-fold>

    private void initDir(Context context, String dirName)
    {
        this.context = context;
        this.dirName = dirName;

        //Find the dir to save cached images
        File primaryDir = context.getCacheDir();
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            primaryDir = android.os.Environment.getExternalStorageDirectory();
        }
        cacheDir = new File(primaryDir, dirName);

        if (!cacheDir.exists())
        {
            synchronized (dirLock)
            {
                if (!cacheDir.mkdirs())
                {
                    Timber.d("Could not create dir %s", cacheDir.getPath());
                }
                else
                {
                    Timber.d("Created dirs %s", cacheDir.getPath());
                }
            }
        }
        else
        {
            Timber.d("Dirs exist %s", cacheDir.getPath());
        }

        collectExistingFiles();
    }

    @Override public void set(String key, Bitmap bitmap)
    {
        super.set(key, bitmap);
        synchronized (dirLock)
        {
            File file = getFile(key);
            try
            {
                OutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Timber.i("Set key %s to %s succeeded", key, file.getPath());
            }
            catch (FileNotFoundException e)
            {
                initDir(context, dirName); // Perhaps the external storage was removed.
            }
            catch (IOException e)
            {
                Timber.e("Set key %s to %s failed", key, file.getPath(), e);
            }
        }
    }

    @Override public Bitmap get(String key)
    {
        Bitmap bitmap = super.get(key);

        if (bitmap == null)
        {
            if (!cacheDir.exists())
            {
                initDir(context, dirName);
            }

            synchronized (dirLock)
            {

                bitmap = BitmapFactory.decodeFile(getFile(key).getPath());
            }

            // Put back in the memory if it was missing
            if (bitmap != null)
            {
                super.set(key, bitmap);
                Timber.i("Got key from disk %s", key);
                Timber.i("Mem cache size %d", size());
            }
        }
        else
        {
            Timber.i("Got key from memory %s", key);
        }

        return bitmap;
    }

    public File getFile(String key)
    {
        return new File(cacheDir, hashKey(key));
    }

    public String hashKey (String key)
    {
        return Base64.encodeToString(getMd5().digest(key.getBytes()), Base64.NO_WRAP).replace('=', '-').replace('/', '-');
    }

    public void clearDir()
    {
        super.clear();
        for (File file: cacheDir.listFiles())
        {
            file.delete();
        }
    }

    private void collectExistingFiles()
    {
        synchronized (dirLock)
        {
            for (File file: cacheDir.listFiles())
            {
                String previous = map.put(file.getPath(), null);
                size++;
            }
        }
    }

    //private void trimToSize(int maxSize) {
    //    while (true)
    //    {
    //        String key;
    //        String value;
    //        synchronized (dirLock)
    //        {
    //            if (size < 0 || (map.isEmpty() && size != 0))
    //            {
    //                throw new IllegalStateException(
    //                        getClass().getName() + ".sizeOf() is reporting inconsistent results!");
    //            }
    //
    //            if (size <= maxSize || map.isEmpty()) {
    //                break;
    //            }
    //
    //            Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
    //            key = toEvict.getKey();
    //            value = toEvict.getValue();
    //            map.remove(key);
    //            size -= Utils.getBitmapBytes(value);
    //            evictionCount++;
    //        }
    //    }
    //}
}
