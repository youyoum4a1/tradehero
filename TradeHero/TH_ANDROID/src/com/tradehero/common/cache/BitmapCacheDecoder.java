package com.tradehero.common.cache;

import android.graphics.Bitmap;
import android.os.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_DISPLAY;

/** Created with IntelliJ IDEA. User: xavier Date: 9/11/13 Time: 5:27 PM To change this template use File | Settings | File Templates. */
public class BitmapCacheDecoder extends ThreadPoolExecutor
{
    private static final int DEFAULT_THREAD_COUNT = 1;

    public BitmapCacheDecoder ()
    {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<Runnable>(), new CacheDecoderThreadFactory());
    }

    public void decode (final File cachedFile, final Target target)
    {
        submit(new Runnable()
        {
            @Override public void run()
            {
                if (cachedFile.exists())
                {
                    try
                    {
                        target.onBitmapLoaded(BitmapFiler.decode(cachedFile), Picasso.LoadedFrom.DISK);
                    } catch (IOException ignored)
                    {
                        target.onBitmapFailed();
                    }
                }
                else
                {
                    target.onBitmapFailed();
                }
            }
        });
    }

    static class CacheDecoderThreadFactory implements ThreadFactory
    {
        @SuppressWarnings("NullableProblems")
        public Thread newThread(Runnable r)
        {
            return new CacheDecoderThread(r);
        }
    }

    private static class CacheDecoderThread extends Thread
    {
        public CacheDecoderThread(Runnable r)
        {
            super(r);
        }

        @Override public void run()
        {
            android.os.Process.setThreadPriority(THREAD_PRIORITY_DISPLAY);
            super.run();
        }
    }

}
