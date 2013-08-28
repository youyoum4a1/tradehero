package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 8/28/13 Time: 6:22 PM Copyright (c) TradeHero */
public class ServerValidatedText extends SelfValidatedText
{
    private int progressIndicatorId;

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

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ServerValidatedText);
        progressIndicatorId = a.getResourceId(R.styleable.ServerValidatedText_progressIndicator, 0);
        a.recycle();
    }

    public void handleServerRequest(boolean requesting)
    {
        if (progressIndicatorId != 0)
        {
            View progressIndicator = getRootView().findViewById(progressIndicatorId);
            if (progressIndicator != null)
            {
                progressIndicator.setVisibility(requesting ?  View.VISIBLE : View.GONE);
            }
        }
    }
}
