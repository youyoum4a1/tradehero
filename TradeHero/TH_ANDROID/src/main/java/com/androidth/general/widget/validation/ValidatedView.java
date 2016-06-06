package com.androidth.general.widget.validation;

import android.support.annotation.NonNull;

public interface ValidatedView
{
    void setStatus(@NonNull Status status);
    @NonNull ValidatedText.Status getStatus();

    public enum Status
    {
        QUIET, VALID, CHECKING, INVALID
    }
}
