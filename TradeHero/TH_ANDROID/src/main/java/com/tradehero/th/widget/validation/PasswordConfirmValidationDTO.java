package com.ayondo.academy.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.ayondo.academy.R;

public class PasswordConfirmValidationDTO extends ValidationDTO
{
    @NonNull public final String passwordDoNotMatchMessage;

    public PasswordConfirmValidationDTO(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        super(context, attrs);
        this.passwordDoNotMatchMessage = context.getString(R.string.password_validation_confirm_fail_string);
    }
}
