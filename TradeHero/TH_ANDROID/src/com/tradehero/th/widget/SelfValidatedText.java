package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 2:58 PM Copyright (c) TradeHero */
public class SelfValidatedText extends ValidatedText
{

    private static final long TIME_TO_WAIT = 1000;

    private TimerTask timerTask;
    private Timer validateTimer;
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

    @Override protected void finalize() throws Throwable
    {
        validateTimer.cancel();
        super.finalize();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        validateTimer = new Timer();
        validateRunnable = new Runnable()
        {
            @Override public void run()
            {
                setValid(validate());
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
        conditionalDelayValidation();
    }

    @Override protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!hasHadInteraction && text != null && text.length() > 0)
        {
            hasHadInteraction = true;
        }
        conditionalDelayValidation();
    }

    protected void conditionalDelayValidation()
    {
        if (hasHadInteraction)
        {
            delayValidation();
        }
    }

    protected void delayValidation()
    {
        if (validateTimer == null) return;
        if (timerTask != null)
        {
            timerTask.cancel();
        }
        timerTask = new TimerTask()
        {
            @Override public void run()
            {

                SelfValidatedText.this.post(validateRunnable);
            }
        };
        validateTimer.purge();
        validateTimer.schedule(timerTask, TIME_TO_WAIT);
    }

    protected boolean validate()
    {
        return (getText() != null && getText().length() > 0) || allowEmpty;
    }
}
