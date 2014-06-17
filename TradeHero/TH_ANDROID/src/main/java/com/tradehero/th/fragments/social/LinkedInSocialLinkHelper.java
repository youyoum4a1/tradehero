package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.LinkedInUtils;
import dagger.Lazy;

import javax.inject.Inject;

public class LinkedInSocialLinkHelper extends SocialLinkHelper
{
    @Inject Lazy<LinkedInUtils> linkedInUtilsLazy;

    public LinkedInSocialLinkHelper(Activity context)
    {
        super(context);
        DaggerUtils.inject(this);
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        linkedInUtilsLazy.get().logIn(context, logInCallback);
    }

    protected int getLinkDialogTitle()
    {
        return R.string.linkedin;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_connecting_to_linkedin;
    }

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.LN;
    }
}
