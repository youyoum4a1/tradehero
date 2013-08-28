package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 2:58 PM Copyright (c) TradeHero */
public class SelfValidatedText extends ValidatedText
{
    private static final long TIME_TO_WAIT = 1000;

    private Runnable validateRunnable;

    private boolean hasHadInteraction = false;

    public SelfValidatedText(Context context)
    {
        super(context);
    }

    public SelfValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SelfValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        validateRunnable = new Runnable()
        {
            @Override public void run()
            {
                conditionalValidation();
            }
        };
    }

    @Override public void onFocusChange(View view, boolean hasFocus)
    {
        super.onFocusChange(view, hasFocus);
        if (!hasFocus)
        {
            // This means the player has moved away
            // It assumes that this method is not called as part of the constructor.
            hasHadInteraction = true;
        }
        conditionalValidation();
    }

    @Override protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (!hasHadInteraction && text != null && text.length() > 0)
        {
            hasHadInteraction = true;
        }

        if (validateRunnable != null)
        {
            this.removeCallbacks(validateRunnable);
            this.postDelayed(validateRunnable, TIME_TO_WAIT);
        }

    }

    protected void conditionalValidation()
    {
        if (hasHadInteraction)
        {
            setValid(validate());
        }
    }

    protected boolean validate()
    {
        return (getText() != null && getText().length() > 0) || allowEmpty;
    }
}
