package com.tradehero.th.fragments.security;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.squareup.widgets.AspectRatioImageView;
import com.tradehero.thm.R;

public class ChartImageView extends AspectRatioImageView
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
    //</editor-fold>

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartImageView);
        includeVolume = a.getBoolean(R.styleable.ChartImageView_includeVolume, false);
        a.recycle();
    }

}
