package com.androidth.general.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.user.UserAvailabilityCacheRx;
import javax.inject.Inject;

public class DisplayNameValidatedText extends ValidatedText
{
    @Inject UserAvailabilityCacheRx userAvailabilityCache;
    public final DisplayNameValidationDTO displayNameValidationDTO;

    //<editor-fold desc="Constructors">
    public DisplayNameValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        this.displayNameValidationDTO = new DisplayNameValidationDTO(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override public DisplayNameValidator getValidator()
    {
        return new DisplayNameValidator(getResources(), displayNameValidationDTO, userAvailabilityCache);
    }
}
