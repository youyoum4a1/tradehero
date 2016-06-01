package com.ayondo.academy.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

public class MatchingPasswordText extends PasswordValidatedText
{
    public final PasswordConfirmValidationDTO passwordConfirmValidationDTO;

    //<editor-fold desc="Constructors">
    public MatchingPasswordText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.passwordConfirmValidationDTO = new PasswordConfirmValidationDTO(context, attrs);
    }

    public MatchingPasswordText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.passwordConfirmValidationDTO = new PasswordConfirmValidationDTO(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override public PasswordConfirmTextValidator getValidator()
    {
        return new PasswordConfirmTextValidator(getResources(), passwordConfirmValidationDTO, isRealSocialNetwork());
    }
}
