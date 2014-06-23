package com.tradehero.th.widget;

import android.view.View;

public class ValidationMessage
{
    private final View sender;
    private final String message;
    private final boolean status;

    //<editor-fold desc="Constructors">
    public ValidationMessage(View sender, boolean status, String message)
    {
        this.sender = sender;
        this.status = status;
        this.message = message;
    }
    //</editor-fold>

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
}
