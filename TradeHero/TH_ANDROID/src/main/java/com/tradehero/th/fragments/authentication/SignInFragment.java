package com.tradehero.th.fragments.authentication;

import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.utils.LocalyticsConstants;
import javax.inject.Inject;

public class SignInFragment extends SignInOrUpFragment
{
    @Inject LocalyticsSession localyticsSession;

    @Override protected int getViewId()
    {
        return R.layout.authentication_sign_in;
    }

    @Override protected int getEmailSignUpViewId()
    {
        return R.id.authentication_email_sign_in_link;
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignIn;
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.SignIn);
    }
}
