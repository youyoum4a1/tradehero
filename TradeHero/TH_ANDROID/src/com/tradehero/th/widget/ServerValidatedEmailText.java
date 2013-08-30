package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:25 AM Copyright (c) TradeHero */
public class ServerValidatedEmailText extends ServerValidatedText
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

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        // TODO when there is a check with the server, we want to be more precise
        return new ValidationMessage(this, isValid(), null);
    }
}
