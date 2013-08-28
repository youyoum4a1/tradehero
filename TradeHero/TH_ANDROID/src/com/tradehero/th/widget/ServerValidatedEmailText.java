package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:25 AM Copyright (c) TradeHero */
public class ServerValidatedEmailText extends SelfValidatedText
{


    public ServerValidatedEmailText(Context context)
    {
        super(context);
    }

    public ServerValidatedEmailText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ServerValidatedEmailText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected boolean validate()
    {
        return super.validate();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
