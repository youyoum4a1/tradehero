package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 8/28/13 Time: 6:22 PM Copyright (c) TradeHero */
public class ServerValidatedText extends SelfValidatedText
{
    private int progressIndicatorId;
    private boolean requesting;
    private View progressIndicator;

    //<editor-fold desc="Constructors">
    public ServerValidatedText(Context context)
    {
        super(context);
    }

    public ServerValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ServerValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ServerValidatedText);
        progressIndicatorId = a.getResourceId(R.styleable.ServerValidatedText_progressIndicator, 0);
        a.recycle();
    }

    public void handleServerRequest(boolean requesting)
    {
        this.requesting = requesting;
        if (progressIndicatorId != 0)
        {
            View progressIndicator = getProgressIndicator();
            if (progressIndicator != null)
            {
                hintValidStatusRight();
                progressIndicator.setVisibility(requesting ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override protected void hintValidStatusRight()
    {
        if (requesting && getDefaultDrawableRight() != null)
        {
            replaceCompoundDrawable(2, getDefaultDrawableRight());
        }
        else
        {
            super.hintValidStatusRight();
        }
    }

    //<editor-fold desc="Accessors">
    public void setProgressIndicatorId(int progressIndicatorId)
    {
        if (this.progressIndicator == null || this.progressIndicator.getId() != progressIndicatorId)
        {
            setProgressIndicator(getRootView().findViewById(progressIndicatorId));
        }
    }

    public View getProgressIndicator()
    {
        if (progressIndicatorId != 0)
        {
            setProgressIndicatorId(progressIndicatorId);
        }
        return progressIndicator;
    }

    public void setProgressIndicator(View progressIndicator)
    {
        this.progressIndicator = progressIndicator;
        this.progressIndicatorId = progressIndicator == null ? 0 : progressIndicator.getId();
    }
    //</editor-fold>
}
