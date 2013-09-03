package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;

/** Created with IntelliJ IDEA. User: xavier Date: 9/3/13 Time: 1:01 PM To change this template use File | Settings | File Templates. */
abstract public class SignInOrUpFragment extends AuthenticationFragment
{
    abstract protected int getViewId ();

    abstract protected int getEmailSignUpViewId ();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewId(), container, false);

        view.findViewById(R.id.btn_facebook_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_twitter_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_linkedin_signin).setOnClickListener(onClickListener);
        view.findViewById(getEmailSignUpViewId ()).setOnClickListener(onClickListener);
        view.findViewById(R.id.txt_term_of_service_signin).setOnClickListener(onClickListener);

        return view;
    }

    abstract public AuthenticationMode getAuthenticationMode();
}
