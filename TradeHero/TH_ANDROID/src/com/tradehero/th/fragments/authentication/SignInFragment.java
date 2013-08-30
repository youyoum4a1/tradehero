package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;

public class SignInFragment extends AuthenticationFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_sign_in, container, false);

        int[] navigationViewIds = new int[] {
                R.id.btn_facebook_signin,
                R.id.btn_twitter_signin,
                R.id.authentication_email_sign_in_link,
                R.id.btn_linkedin_signin,
                R.id.txt_term_of_service_signin
        };
        for (int id: navigationViewIds)
        {
            view.findViewById(id).setOnClickListener(onClickListener);
        }

        return view;
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignIn;
    }
}
