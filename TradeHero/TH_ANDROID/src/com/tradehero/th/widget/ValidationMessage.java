package com.tradehero.th.widget;

import android.view.View;

/** Created with IntelliJ IDEA. User: tho Date: 8/28/13 Time: 5:41 PM Copyright (c) TradeHero */
public class ValidationMessage
{
    private View sender;

    private String message;

    private boolean status;

    public String getMessage()
    {
        return message;
    }

    public boolean getStatus()
    {
        return status;
    }

    public View getSender()
    {
        return sender;
    }

    public ValidationMessage(View sender, boolean status, String message)
    {
        this.sender = sender;
        this.status = status;
        this.message = message;
    }
}
