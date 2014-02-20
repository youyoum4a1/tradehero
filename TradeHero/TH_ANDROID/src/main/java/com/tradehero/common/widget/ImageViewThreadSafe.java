package com.tradehero.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tradehero.common.utils.THLog;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 9/30/13 Time: 7:38 PM To change this template use File | Settings | File Templates. */
public class ImageViewThreadSafe extends ImageView
{
    public String softId;

    //<editor-fold desc="Constructors">
    public ImageViewThreadSafe(Context context)
    {
        super(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImageViewThreadSafe(Context context, AttributeSet attrs)
    {
        super(context, attrs);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImageViewThreadSafe(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);    //To change body of overridden methods use File | Settings | File Templates.
    }
    //</editor-fold>

    @Override public void setImageBitmap(final Bitmap bm)
    {
        Timber.i("Setting ImageBitmap %s", softId);
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            Timber.i("Setting own.ImageBitmap %s", softId);
            super.setImageBitmap(bm);
            requestLayout();
        }
        else
        {
            post(new Runnable()
            {
                @Override public void run()
                {
                    Timber.i("Setting super.ImageBitmap %s", softId);
                    ImageViewThreadSafe.super.setImageBitmap(bm);
                }
            });
        }
    }

    @Override public void setImageResource(final int resId)
    {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            super.setImageResource(resId);
        }
        else
        {
            post(new Runnable()
            {
                @Override public void run()
                {
                    ImageViewThreadSafe.super.setImageResource(resId);
                }
            });
        }
    }

    @Override public void setImageDrawable(final Drawable drawable)
    {
        //THLog.i(TAG, "Setting ImageDrawable " + softId + " w" + getWidth() + " h" + getHeight() + " url " + getUrl());
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            //THLog.i(TAG, "Setting own.ImageDrawable "
            //        + softId
            //        + " w"
            //        + getWidth()
            //        + " h"
            //        + getHeight()
            //        + " mw"
            //        + getMeasuredWidth()
            //        + " mh"
            //        + getMeasuredHeight()
            //        + " url "
            //        + getTag(R.string.image_url));
            super.setImageDrawable(drawable);
            requestLayout();
        }
        else
        {
            //THLog.i(TAG, "Posting this.ImageDrawable " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getTag(R.string.image_url));
            post(new Runnable()
            {

                @Override public void run()
                {
                    ImageViewThreadSafe.this.setImageDrawable(drawable);
                }
            });
        }
    }
}
