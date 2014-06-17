package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.WeiboUtils;

import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class WeiboSocialLinkHelper extends SocialLinkHelper
{
    @NotNull private final WeiboUtils weiboUtils;

    //<editor-fold desc="Constructors">
    @Inject public WeiboSocialLinkHelper(
            @NotNull CurrentUserId currentUserId,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull WeiboUtils weiboUtils)
    {
        super(currentUserId, progressDialogUtil, socialServiceWrapper, currentActivityHolder);
        this.weiboUtils = weiboUtils;
    }
    //</editor-fold>

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.WB;
    }

    protected int getLinkDialogTitle()
    {
        return R.string.sina_weibo;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_connecting_to_weibo;
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        weiboUtils.logIn(context, logInCallback);
    }
}
