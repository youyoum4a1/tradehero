package com.androidth.general.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.androidth.general.R;

public class DisplayNameValidationDTO extends ValidationDTO
{
    @NonNull public final String displayNameTakenMessage;

    public DisplayNameValidationDTO(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        super(context, attrs);
        this.displayNameTakenMessage = context.getString(R.string.validation_server_username_not_available);
    }
}
