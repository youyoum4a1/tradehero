package com.ayondo.academy.widget.validation;

import android.content.res.Resources;
import android.support.annotation.NonNull;

public class PasswordValidator extends TextValidator
{
    public final boolean allowEmpty;

    public PasswordValidator(
            @NonNull Resources resources,
            @NonNull ValidationDTO validationDTO,
            boolean allowEmpty)
    {
        super(resources, validationDTO);
        this.allowEmpty = allowEmpty;
    }

    @NonNull @Override protected ValidationMessage getValidationMessage()
    {
        if (allowEmpty
                && text.length() == 0)
        {
            return new ValidationMessage(ValidatedView.Status.VALID, null);
        }
        return super.getValidationMessage();
    }
}
