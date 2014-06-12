package com.tradehero.th.fragments.social;

import android.app.Activity;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by tradehero on 14-6-5.
 */
public class FacebookSocialLinkHelper extends SocialLinkHelper {

    @Inject
    Lazy<FacebookUtils> facebookUtils;

    public FacebookSocialLinkHelper(Activity context) {
        super(context);
        DaggerUtils.inject(this);
    }

    protected void doLoginAction(Activity context, LogInCallback logInCallback)
    {
        facebookUtils.get().logIn(context, logInCallback);
    }

    protected int getLinkDialogTitle()
    {
        return R.string.facebook;
    }

    protected int getLinkDialogMessage()
    {
        return R.string.authentication_connecting_to_facebook;
    }

    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.FB;
    }
}
