package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.social.SocialLinkHelper;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.TwitterUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by tradehero on 14-6-5.
 */
public class TwitterSocialLinkHelper extends SocialLinkHelper {


    @Inject
    Lazy<TwitterUtils> twitterUtils;

    public TwitterSocialLinkHelper(Activity context) {
        super(context);
        DaggerUtils.inject(this);
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        twitterUtils.get().logIn(context, logInCallback);
    }

    protected int getLinkDialogTitle()
    {
        return R.string.twitter;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_twitter_connecting;
    }

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.TW;
    }
}
