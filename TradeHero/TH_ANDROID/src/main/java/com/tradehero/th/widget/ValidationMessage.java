package com.tradehero.th.widget;

import android.view.View;

public class ValidationMessage
{
    private View sender;

    private String message;

    private boolean status;

    //<editor-fold desc="Accessors">
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
    //</editor-fold>

    public ValidationMessage(View sender, boolean status, String message)
    {
        this.sender = sender;
        this.status = status;
        this.message = message;
    }
}
