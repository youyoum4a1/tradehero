package com.tradehero.th.fragments.authentication;

import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import javax.inject.Inject;

/**
 * Fragment for signing up
 */
public class SignUpFragment extends SignInOrUpFragment
{
    @Inject THLocalyticsSession localyticsSession;

    @Override protected int getViewId()
    {
        return R.layout.authentication_sign_up;
    }

    @Override protected int getEmailSignUpViewId()
    {
        return R.id.authentication_email_sign_up_link;
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignUp;
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.SignUp);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            localyticsSession.tagEvent(LocalyticsConstants.SignUp_Back);
        }

        return super.onOptionsItemSelected(item);
    }
}
