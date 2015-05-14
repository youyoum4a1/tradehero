package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.auth.AuthenticationMode;

public class SignInFragment extends SignInOrUpFragment
{
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
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.guide_screen_login));
        setHeadViewRight0(getString(R.string.authentication_register));
        setRight0ButtonOnClickListener(onClickListener);
    }

    @Override public void onClickHeadRight0()
    {
        super.onClickHeadRight0();
    }

    @Override public void onClickHeadLeft()
    {
        ActivityHelper.presentFromActivity(getActivity(), GuideActivity.class);
        getActivity().finish();
    }
}
