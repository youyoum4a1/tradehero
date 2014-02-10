package com.tradehero.common.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.PicassoUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Created with IntelliJ IDEA. User: xavier Date: 9/11/13 Time: 7:38 PM To change this template use File | Settings | File Templates. */
public class LruMemFileCache extends LruCache
{
    public static final String TAG = LruMemFileCache.class.getSimpleName();
    public static final String DEFAULT_DIR_NAME = LruMemFileCache.class.getPackage().getName();
    public static final int DEFAULT_BASE_64_PARAM = Base64.NO_PADDING | Base64.NO_WRAP;

    // Here we use only 1 index value per key on the disk cache
    public static final int DEFAULT_INDEX = 0;

    private DiskLruCache diskLruCache;

    private static MessageDigest md5;
    final private Object dirLock = new Object();
    final private Object setLock = new Object();
    final private Object getLock = new Object();

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
    private long maxFileSize;

    //<editor-fold desc="Constructors">
    public LruMemFileCache(Context context)
    {
        this(context, DEFAULT_DIR_NAME);
    }

    public LruMemFileCache(Context context, String dirName)
    {
        super(context);
        THLog.d(TAG, "Constructing with memory " + PicassoUtils.calculateMemoryCacheSize(context));
        initDir(context, getDefaultFolderSizeToUse(getPreferredCacheParentDirectory(context)), dirName);
    }

    public LruMemFileCache(int maxMemSize, long maxFileSize)
    {
        this(maxMemSize, maxFileSize, DEFAULT_DIR_NAME);
    }

    public LruMemFileCache(int maxMemSize, long maxFileSize, String dirName)
    {
        super(maxMemSize);
        initDir(Application.context(), maxFileSize, dirName);
    }
    //</editor-fold>

    private void initDir(Context context, long maxFileSize, String dirName)
    {
        this.context = context;
        this.maxFileSize = maxFileSize;
        this.dirName = dirName;

        //Find the dir to save cached images
        cacheDir = new File(getPreferredCacheParentDirectory(this.context), this.dirName);

        createMissingDir();
        openCacheDir();
    }

    private void createMissingDir()
    {
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
    }

    private void openCacheDir()
    {
        try
        {
            this.diskLruCache = DiskLruCache.open(cacheDir, 0, 1, this.maxFileSize);
        }
        catch (IOException e)
        {
            THLog.e(TAG, "Failed to open a DiskLruCache", e);
            this.diskLruCache = null;
        }
    }

    public File getPreferredCacheParentDirectory(Context context)
    {
        File primaryDir = context.getCacheDir();
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            if (android.os.Environment.getExternalStorageDirectory().canWrite())
            {
                primaryDir = android.os.Environment.getExternalStorageDirectory();
            }
        }
        return primaryDir;
    }

    public long getDefaultFolderSizeToUse(File folder)
    {
        long total = folder.getTotalSpace();
        return total / 10;
        //long free = folder.getFreeSpace();
        //long used = FileUtils.getFolderSize(folder);
        //return (free + used) / 2;
    }

    @Override public void set(String key, Bitmap bitmap)
    {
        super.set(key, bitmap);
        if (diskLruCache != null)
        {
            try
            {
                DiskLruCache.Editor entryEdit = null;
                synchronized (setLock)
                {
                    try
                    {
                        entryEdit = diskLruCache.edit(hashKey(key));
                        OutputStream os = entryEdit.newOutputStream(DEFAULT_INDEX);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.flush();
                        os.close();
                        entryEdit.commit();
                    }
                    catch (IOException e)
                    {
                        THLog.e(TAG, "Failed to save entry " + key, e);
                        if (entryEdit != null)
                        {
                            entryEdit.abortUnlessCommitted();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                THLog.e(TAG, "Failed to save entry " + key, e);
            }
        }
    }

    @Override public Bitmap get(String key)
    {
        Bitmap bitmap = super.get(key);

        if (bitmap == null && diskLruCache != null)
        {
            synchronized (setLock)
            {
                try
                {
                    DiskLruCache.Snapshot retrieved = null;
                    try
                    {
                        retrieved = diskLruCache.get(hashKey(key));
                        if (retrieved != null)
                        {
                            InputStream is = retrieved.getInputStream(DEFAULT_INDEX);
                            bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            if (bitmap != null)
                            {
                                super.set(key, bitmap);
                                //THLog.i(TAG, "Got bitmap from disk " + key);
                            }
                            else
                            {
                                //THLog.i(TAG, "Did not get bitmap from disk in the end " + key);
                            }
                        }
                        else
                        {
                            //THLog.i(TAG, "Retrieved was null for " + key);
                        }
                    }
                    catch (IOException e)
                    {
                        THLog.e(TAG, "Failed to get entry " + key, e);
                    }
                    catch (OutOfMemoryError e)
                    {
                        THLog.e(TAG, "Failed to decode " + key, e);
                    }

                    if (retrieved != null)
                    {
                        retrieved.close();
                    }
                }
                catch (Exception e)
                {
                    THLog.e(TAG, "Failed to get entry " + key, e);
                }
            }
        }
        else
        {
            //THLog.i(TAG, "Got bitmap from ram " + key);
        }

        return bitmap;
    }

    public String hashKey (String key)
    {
        return Base64.encodeToString(getMd5().digest(key.getBytes()), DEFAULT_BASE_64_PARAM).replace('/', 'b').replace('+', 'c').toLowerCase();
    }

    public void flush()
    {
        super.clear();
        synchronized (dirLock)
        {
            if (diskLruCache != null)
            {
                try
                {
                    diskLruCache.delete();
                }
                catch (IOException e)
                {
                    THLog.e(TAG, "Failed to flush", e);
                    THToast.show(R.string.error_cache_flush);
                }
                finally
                {
                    createMissingDir();
                    openCacheDir();
                }
            }
        }
    }
}
