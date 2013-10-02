package com.tradehero.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.tradehero.common.utils.THLog;

/** Created with IntelliJ IDEA. User: xavier Date: 9/11/13 Time: 1:37 PM To change this template use File | Settings | File Templates. */
public class ImageUrlView extends ImageView
{
    public static final String TAG = ImageUrlView.class.getSimpleName();

    private String url;
    public String softId;

    //<editor-fold desc="Constructors">
    public ImageUrlView(Context context)
    {
        super(context);
    }

    public ImageUrlView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ImageUrlView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
/*
    @Override public void setImageBitmap(final Bitmap bm)
    {
        THLog.i(TAG, "Setting ImageBitmap " + softId);
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            THLog.i(TAG, "Setting own.ImageBitmap " + softId);
            super.setImageBitmap(bm);
            requestLayout();
        }
        else
        {
            post(new Runnable()
            {
                @Override public void run()
                {
                    THLog.i(TAG, "Setting super.ImageBitmap " + softId);
                    ImageUrlView.super.setImageBitmap(bm);
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
                    ImageUrlView.super.setImageResource(resId);
                }
            });
        }
    }

    @Override public void setImageDrawable(final Drawable drawable)
    {
        //THLog.i(TAG, "Setting ImageDrawable " + softId + " w" + getWidth() + " h" + getHeight() + " url " + getUrl());
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            THLog.i(TAG, "Setting own.ImageDrawable " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
            super.setImageDrawable(drawable);
            requestLayout();
        }
        else
        {
            THLog.i(TAG, "Posting this.ImageDrawable " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
            post(new Runnable()
            {

                @Override public void run()
                {
                    ImageUrlView.this.setImageDrawable(drawable);
                    //invalidate();
                }
            });
        }
    }*/

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        //THLog.i(TAG, "Attached to window " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
    }

    @Override protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);
        //THLog.i(TAG, "Visibility changed " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //THLog.i(TAG, "On Measure " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
    }

    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //THLog.i(TAG, "On Draw " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        //THLog.i(TAG, "On FinishInflate " + softId + " w" + getWidth() + " h" + getHeight() + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        //THLog.i(TAG, "On Size Changed " + softId + " w" + getWidth() + " h" + getHeight() + " oldw" + oldw + " oldh" + oldh + " mw" + getMeasuredWidth() + " mh" + getMeasuredHeight() + " url " + getUrl());
    }
}
