package com.androidth.general.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.user.UserEmailAvailabilityCacheRx;

import javax.inject.Inject;

/**
 * Created by ayushnvijay on 6/13/16.
 */
public class EmailValidatedText extends ValidatedText {

    @Inject
    UserEmailAvailabilityCacheRx emailAvailabilityCache;
    public final EmailValidationDTO emailValidationDTO;

    public EmailValidatedText(Context context, AttributeSet attrs) {
        super(context, attrs);
        HierarchyInjector.inject(this);
        this.emailValidationDTO = new EmailValidationDTO(context,attrs);
    }

    @NonNull
    @Override public EmailValidator getValidator()
    {
        return new EmailValidator(getResources(), emailValidationDTO, emailAvailabilityCache);
    }
}
