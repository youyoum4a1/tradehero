package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.thm.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LinkedInSocialLinkHelper extends SocialLinkHelper
{
    @NotNull private final LinkedInUtils linkedInUtils;

    //<editor-fold desc="Constructors">
    @Inject public LinkedInSocialLinkHelper(
            @NotNull CurrentUserId currentUserId,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull LinkedInUtils linkedInUtils)
    {
        super(currentUserId, progressDialogUtil, socialServiceWrapper, currentActivityHolder);
        this.linkedInUtils = linkedInUtils;
    }
    //</editor-fold>

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.LN;
    }

    protected int getLinkDialogTitle()
    {
        return R.string.linkedin;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_connecting_to_linkedin;
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        linkedInUtils.logIn(context, logInCallback);
    }
}
