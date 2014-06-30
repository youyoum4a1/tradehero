package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.thm.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class FacebookSocialLinkHelper extends SocialLinkHelper
{
    @NotNull private final FacebookUtils facebookUtils;

    //<editor-fold desc="Constructors">
    @Inject public FacebookSocialLinkHelper(
            @NotNull CurrentUserId currentUserId,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull FacebookUtils facebookUtils)
    {
        super(currentUserId, progressDialogUtil, socialServiceWrapper, currentActivityHolder);
        this.facebookUtils = facebookUtils;
    }
    //</editor-fold>

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.FB;
    }

    protected int getLinkDialogTitle()
    {
        return R.string.facebook;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_connecting_to_facebook;
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        facebookUtils.logIn(context, logInCallback);
    }
}
