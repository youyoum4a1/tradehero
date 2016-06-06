package com.androidth.general.widget.validation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;

public class PasswordConfirmTextValidator extends PasswordValidator
{
    @Nullable private CharSequence mainPassword;

    public PasswordConfirmTextValidator(
            @NonNull Resources resources,
            @NonNull PasswordConfirmValidationDTO validationDTO,
            boolean allowEmpty)
    {
        super(resources, validationDTO, allowEmpty);
    }

    public void setMainPassword(@Nullable CharSequence mainPassword)
    {
        this.mainPassword = mainPassword;
    }

    @NonNull public TextWatcher getPasswordTextWatcher()
    {
        return new TextWatcher()
        {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mainPassword = s;
                delayedValidate();
            }

            @Override public void afterTextChanged(Editable s)
            {
            }
        };
    }

    @NonNull @Override protected ValidationMessage getValidationMessage()
    {
        if (needsToHintValidStatus() && needsToValidate())
        {
            if (mainPassword != null && !mainPassword.toString().equals(text.toString()))
            {
                return new ValidationMessage(ValidatedView.Status.INVALID, ((PasswordConfirmValidationDTO) validationDTO).passwordDoNotMatchMessage);
            }
        }
        return super.getValidationMessage();
    }
}
