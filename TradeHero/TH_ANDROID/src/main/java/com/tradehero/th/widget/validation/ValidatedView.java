package com.tradehero.th.widget.validation;

import android.support.annotation.NonNull;

public interface ValidatedView
{
    void setStatus(@NonNull Status status);
    @NonNull ValidatedText.Status getStatus();

    enum Status
    {
        QUIET, VALID, CHECKING, INVALID
    }
}
