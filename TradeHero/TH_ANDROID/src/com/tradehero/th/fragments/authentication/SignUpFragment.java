package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;

public class SignUpFragment extends AuthenticationFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_sign_up, container, false);

        view.findViewById(R.id.btn_facebook_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.authentication_email_sign_up_link).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_twitter_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.txt_term_of_service_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_linkedin_signin).setOnClickListener(onClickListener);

        return view;
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignUp;
    }
}
