package com.tradehero.common.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.jakewharton.disklrucache;
import com.squareup.picasso.LruCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.application.App;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/11/13 Time: 7:38 PM To change this template use File | Settings | File Templates. */
public class LruMemFileCache extends LruCache
{
    public static final String TAG = LruMemFileCache.class.getSimpleName();
    public static final String DEFAULT_DIR_NAME = LruMemFileCache.class.getPackage().getName();
    public static final int DEFAULT_MAX_FILE_SIZE = 200;

    private DiskLruCache diskLruCache;

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
    public LruMemFileCache(Context context)
    {
        super(context);
        this.maxFileSize = DEFAULT_MAX_FILE_SIZE;
        this.map = new LinkedHashMap<String, String>(0, 0.75f, true);
        initDir(context, DEFAULT_DIR_NAME);
    }

    public LruMemFileCache(int maxMemSize, int maxFilesize)
    {
        super(maxMemSize);
        this.maxFileSize = maxFileSize;
        this.map = new LinkedHashMap<String, String>(0, 0.75f, true);
        initDir(App.context(), DEFAULT_DIR_NAME);
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
                    Log.d(TAG, "Could not create dir " + cacheDir.getPath());
                }
                else
                {
                    Log.d(TAG, "Created dirs " + cacheDir.getPath());
                }
            }
        }
        else
        {
            Log.d(TAG, "Dirs exist " + cacheDir.getPath());
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
                THLog.i(TAG, "Set key " + key + " to " + file.getPath() + " succeeded ");
            }
            catch (FileNotFoundException e)
            {
                initDir(context, dirName); // Perhaps the external storage was removed.
            }
            catch (IOException e)
            {
                THLog.e(TAG, "Set key " + key + " to " + file.getPath() + " failed ", e);
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
                THLog.i(TAG, "Got key from disk " + key);
                THLog.i(TAG, "Mem cache size " + size());
            }
        }
        else
        {
            THLog.i(TAG, "Got key from memory " + key);
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
}
