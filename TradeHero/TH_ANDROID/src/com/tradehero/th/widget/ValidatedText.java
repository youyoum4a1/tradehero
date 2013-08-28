package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:24 AM Copyright (c) TradeHero */
public class ValidatedText extends EditText implements ValidatedView, View.OnFocusChangeListener
{
    protected boolean allowEmpty;
    private int indicator;
    protected boolean isValid = true;
    private Drawable invalidDrawable;
    private Drawable validDrawable;

    public ValidatedText(Context context)
    {
        super(context);
    }

    public ValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public ValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatedText);
        allowEmpty = a.getBoolean(R.styleable.ValidatedText_allowEmpty, false);
        indicator = a.getResourceId(R.styleable.ValidatedText_indicator, 0);
        invalidDrawable = a.getDrawable(R.styleable.ValidatedText_invalidDrawable);
        a.recycle();
        validDrawable = this.getCompoundDrawables()[0];
        this.setOnFocusChangeListener(this);
    }

    @Override public void onFocusChange(View view, boolean hasFocus)
    {
        updateViewForStatus();
    }

    protected void setValid(boolean isValidated)
    {
        this.isValid = isValidated;

        updateViewForStatus();
    }

    private void updateViewForStatus()
    {
        if (!isValid && invalidDrawable != null)
        {
            invalidDrawable.setBounds(validDrawable.getBounds());
            this.setCompoundDrawables(invalidDrawable, null, null, null);
        }
        else if (isValid && invalidDrawable != null)
        {
            this.setCompoundDrawables(validDrawable, null, null, null);
        }
    }

    public void setIndicator(int indicator)
    {
        this.indicator = indicator;
    }

    public int getIndicator()
    {
        return indicator;
    }

    @Override public boolean getIsValid()
    {
        return isValid;
    }
}
