package com.ayondo.academy.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.persistence.prefs.AuthHeader;
import javax.inject.Inject;

public class PasswordValidatedText extends ValidatedText
{
    @Inject @AuthHeader @Nullable protected String authToken;

    //<editor-fold desc="Constructors">
    public PasswordValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PasswordValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void init(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        super.init(context, attrs);
        if (!isInEditMode())
        {
            HierarchyInjector.inject(this);
        }
    }

    @NonNull @Override public TextValidator getValidator()
    {
        return new PasswordValidator(getResources(), validationDTO, isRealSocialNetwork());
    }

    protected boolean isRealSocialNetwork()
    {
        for (SocialNetworkEnum socialNetworkEnum : SocialNetworkEnum.values())
        {
            if (authToken != null
                    && !socialNetworkEnum.equals(SocialNetworkEnum.TH)
                    && socialNetworkEnum.isLogin(authToken))
            {
                return true;
            }
        }
        return false;
    }
}
