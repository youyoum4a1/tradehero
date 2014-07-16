package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;

public class SignInFragment extends SignInOrUpFragment
{
    @Inject Analytics analytics;

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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewId(), container, false);
        setOnClickListener(view);
        view.findViewById(R.id.authentication_by_sign_up_button).setOnClickListener(onClickListener);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.SignIn));
    }
}
