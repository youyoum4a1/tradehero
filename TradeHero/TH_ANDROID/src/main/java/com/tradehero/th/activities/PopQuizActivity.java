package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.web.XWalkWebViewFragment;
import com.tradehero.th.persistence.prefs.AuthHeader;
import javax.inject.Inject;

public class PopQuizActivity extends OneFragmentActivity
{
    private static final String POPQUIZ_HOME_URL = "https://fb.tradehero.mobi/PopQuizWeb/Home";
    private static final String POPQUIZ_ACCESS_TOKEN_KEY = "accessToken";

    @Inject @AuthHeader String thAuthHeader;

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return XWalkWebViewFragment.class;
    }

    @NonNull @Override protected Bundle getInitialBundle()
    {
        Bundle args = super.getInitialBundle();

        if (thAuthHeader.startsWith(SocialNetworkEnum.FB.getAuthHeader()))
        {
            String[] splits = thAuthHeader.split(" ");
            XWalkWebViewFragment.putUrl(args, POPQUIZ_HOME_URL + "?" + POPQUIZ_ACCESS_TOKEN_KEY + "=" + splits[1]);
        }
        else
        {
            THToast.show("Please login with Facebook to play PopQuiz");
        }
        return args;
    }
}
