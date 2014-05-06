package com.tradehero.common.cache;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.util.Base64;
import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.picasso.LruCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;


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
    /**public*/ LruMemFileCache(Context context)
    {
        this(context, DEFAULT_DIR_NAME);

    }
    public static LruMemFileCache instance;
    public static synchronized LruMemFileCache getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new LruMemFileCache(context);
        }
        return instance;
    }

    private static final int MAX_MEM_CACHE_SIZE = 30 * 1024 * 1024; // 30MB

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }
    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target 16% of the available RAM.
        int size = 1024 * 1024 * memoryClass / 10;
        // Bound to max size for mem cache.
        return Math.min(size, MAX_MEM_CACHE_SIZE);
    }

    /**public*/ LruMemFileCache(Context context, String dirName)
    {
        super(calculateMemoryCacheSize(context));
        initDir(context, getDefaultFolderSizeToUse(getPreferredCacheParentDirectory(context)), dirName);
    }


    /**public*/ LruMemFileCache(int maxMemSize, long maxFileSize)
    {
        this(maxMemSize, maxFileSize, DEFAULT_DIR_NAME);
    }

    /**public*/ LruMemFileCache(int maxMemSize, long maxFileSize, String dirName)
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
    }

    private void openCacheDir()
    {
        try
        {
            this.diskLruCache = DiskLruCache.open(cacheDir, 0, 1, this.maxFileSize);
        }
        catch (IOException e)
        {
            Timber.e("Failed to open a DiskLruCache", e);
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

    public File getCacheDirectory()
    {
        return cacheDir;
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
        //test
        super.set(key, bitmap);
        //Log.d(TAG, TAG + " set method main ?" + (Looper.getMainLooper().getThread() == Thread.currentThread()) + " key:" +key+" mem size "+super.size());
        if (diskLruCache != null)
        {
            try
            {
                DiskLruCache.Editor entryEdit = null;
                synchronized (setLock)
                {
                    try
                    {
                        //String hashKey = hashKey(key);
                        entryEdit = diskLruCache.edit(key);
                        //entryEdit = diskLruCache.edit(hashKey(key));
                        OutputStream os = entryEdit.newOutputStream(DEFAULT_INDEX);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.flush();
                        os.close();
                        entryEdit.commit();
                    }
                    catch (IOException e)
                    {
                        Timber.e("Failed to save entry %s", key, e);
                        if (entryEdit != null)
                        {
                            entryEdit.abortUnlessCommitted();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Timber.e("Failed to save entry %s", key, e);
            }
        }
    }

    //    public Bitmap getAsync(String key){
    //
    //        Log.d(TAG,TAG+" getAsync method main? "+(Looper.getMainLooper().getThread() == Thread.currentThread())+" key "+key
    //                +" mem size "+super.size()+">>>");
    //
    //        Bitmap bitmap = super.get(key);
    //
    //
    //        if (bitmap == null && diskLruCache != null)
    //        {
    //            synchronized (setLock)
    //            {
    //                try
    //                {
    //                    DiskLruCache.Snapshot retrieved = null;
    //                    try
    //                    {
    //                        String hashKey = hashKey(key);
    //                        retrieved = diskLruCache.get(hashKey);
    //                        Log.d(TAG,TAG+" getAsync from discache hashKey:"+hashKey);
    //                        if (retrieved != null)
    //                        {
    //                            InputStream is = retrieved.getInputStream(DEFAULT_INDEX);
    //                            bitmap = BitmapFactory.decodeStream(is);
    //                            is.close();
    //                            if (bitmap != null)
    //                            {
    //                                super.set(key, bitmap);
    //                                //THLog.i(TAG, "Got bitmap from disk " + key);
    //                                Log.d(TAG,TAG+" getAsync from discache and set to memcache hashKey:"+hashKey);
    //                            }
    //                            else
    //                            {
    //                                //THLog.i(TAG, "Did not get bitmap from disk in the end " + key);
    //                            }
    //                        }
    //                        else
    //                        {
    //                            //THLog.i(TAG, "Retrieved was null for " + key);
    //                        }
    //                    }
    //                    catch (IOException e)
    //                    {
    //                        Timber.e("Failed to get entry %s", key, e);
    //                    }
    //                    catch (OutOfMemoryError e)
    //                    {
    //                        Timber.e("Failed to decode %s", key, e);
    //                    }
    //
    //                    if (retrieved != null)
    //                    {
    //                        retrieved.close();
    //                    }
    //                }
    //                catch (Exception e)
    //                {
    //                    Timber.e("Failed to get entry %s", key, e);
    //                }
    //            }
    //        }
    //        else
    //        {
    //            //THLog.i(TAG, "Got bitmap from ram " + key);
    //        }
    //
    //        Log.d(TAG,TAG+" getAsync method main ?"+(Looper.myLooper()==Looper.getMainLooper())+" return:"+bitmap+" >>>");
    //
    //        return bitmap;
    //    }

    @Override public Bitmap get(String key)
    {
        boolean isUIThread = Looper.myLooper()==Looper.getMainLooper();

        //Log.d(TAG, TAG + " get method main? " + (Looper.getMainLooper().getThread() == Thread.currentThread()) + " key " + key+ " mem size " + super.size() + ">>>");
        Bitmap bitmap = super.get(key);
        if (bitmap == null && diskLruCache != null && !isUIThread)
        {
            synchronized (setLock)
            {
                try
                {
                    DiskLruCache.Snapshot retrieved = null;
                    try
                    {
                        //String hashKey = hashKey(key);
                        retrieved = diskLruCache.get(key);
                        //String hashKey = hashKey(key);
                        //retrieved = diskLruCache.get(hashKey);
                        Timber.d(TAG,TAG+" get from discache key:"+key);
                        if (retrieved != null)
                        {
                            InputStream is = retrieved.getInputStream(DEFAULT_INDEX);
                            bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            if (bitmap != null)
                            {
                                super.set(key, bitmap);
                                //THLog.i(TAG, "Got bitmap from disk " + key);
                                //Log.d(TAG,TAG+" get from discache and set to memcache key:"+key);
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
                        Timber.e("Failed to get entry %s", key, e);
                    }
                    catch (OutOfMemoryError e)
                    {
                        Timber.e("Failed to decode %s", key, e);
                    }

                    if (retrieved != null)
                    {
                        retrieved.close();
                    }
                }
                catch (Exception e)
                {
                    Timber.e("Failed to get entry %s", key, e);
                }
            }
        }
        else
        {
            //THLog.i(TAG, "Got bitmap from ram " + key);
        }
        //Log.d(TAG,TAG+" get method main ?"+isUIThread+" return:"+bitmap+" >>>");
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
                    Timber.e("Failed to flush", e);
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
