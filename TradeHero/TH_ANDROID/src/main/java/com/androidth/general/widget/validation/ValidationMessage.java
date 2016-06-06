package com.androidth.general.widget.validation;

import android.support.annotation.NonNull;

public class ValidationMessage
{
    private final String message;
    @Deprecated
    private final boolean status;
    @NonNull private final ValidatedView.Status validStatus;

    //<editor-fold desc="Constructors">
    @Deprecated
    public ValidationMessage(boolean status, String message)
    {
        this.status = status;
        this.message = message;
        this.validStatus = status ? ValidatedView.Status.VALID : ValidatedView.Status.INVALID;
    }

    public ValidationMessage(@NonNull ValidatedView.Status status, String message)
    {
        this.status = true;
        this.message = message;
        this.validStatus = status;
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public String getMessage()
    {
        return message;
    }

    @Deprecated
    public boolean getStatus()
    {
        return status;
    }

    @NonNull public ValidatedView.Status getValidStatus()
    {
        return validStatus;
    }
    //</editor-fold>
}
