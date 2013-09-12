package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** Created with IntelliJ IDEA. User: xavier Date: 9/11/13 Time: 1:37 PM To change this template use File | Settings | File Templates. */
public class ImageUrlView extends ImageView
{
    public static final String TAG = ImageUrlView.class.getSimpleName();

    private String url;

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
}
