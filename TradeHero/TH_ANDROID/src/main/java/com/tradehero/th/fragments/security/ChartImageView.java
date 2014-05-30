package com.tradehero.th.fragments.security;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tradehero.th.R;

public class ChartImageView extends ImageView
{
    public boolean includeVolume;

    //<editor-fold desc="Constructors">
    public ChartImageView(Context context)
    {
        super(context);
    }

    public ChartImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public ChartImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    //</editor-fold>

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartImageView);
        includeVolume = a.getBoolean(R.styleable.ChartImageView_includeVolume, false);
        a.recycle();
    }

}
