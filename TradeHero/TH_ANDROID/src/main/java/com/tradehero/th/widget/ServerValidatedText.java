package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.R;

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

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        progressIndicator = getRootView().findViewById(progressIndicatorId);
        if (progressIndicator != null)
        {
            progressIndicator.setVisibility(INVISIBLE);
        }
    }

    public void handleServerRequest(boolean requesting)
    {
        this.requesting = requesting;
        hintValidStatusRight();
    }

    @Override protected void hintValidStatusRight()
    {
        View progressIndicatorCopy = progressIndicator;
        if (requesting && progressIndicatorCopy != null)
        {
            progressIndicatorCopy.setVisibility(VISIBLE);
            if (getDefaultDrawableRight() != null)
            {
                replaceCompoundDrawable(INDEX_RIGHT, getDefaultDrawableRight());
            }
        }
        else
        {
            if (!requesting && progressIndicatorCopy != null)
            {
                progressIndicatorCopy.setVisibility(INVISIBLE);
            }
            super.hintValidStatusRight();
        }
    }
}
