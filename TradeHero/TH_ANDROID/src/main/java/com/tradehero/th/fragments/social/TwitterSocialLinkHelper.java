package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.TwitterUtils;

import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class TwitterSocialLinkHelper extends SocialLinkHelper
{
    @NotNull private final TwitterUtils twitterUtils;

    //<editor-fold desc="Constructors">
    @Inject public TwitterSocialLinkHelper(
            @NotNull CurrentUserId currentUserId,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull TwitterUtils twitterUtils)
    {
        super(currentUserId, progressDialogUtil, socialServiceWrapper, currentActivityHolder);
        this.twitterUtils = twitterUtils;
    }
    //</editor-fold>

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.TW;
    }

    protected int getLinkDialogTitle()
    {
        return R.string.twitter;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_twitter_connecting;
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        twitterUtils.logIn(context, logInCallback);
    }
}
