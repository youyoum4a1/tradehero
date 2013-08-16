package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;

public class SignInFragment extends AuthenticationFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_sign_in, container, false);

        View[] navigationViews = new View[] {
                view.findViewById(R.id.btn_facebook_signin),
                view.findViewById(R.id.btn_twitter_signin),
                view.findViewById(R.id.txt_email_sign_in),
                view.findViewById(R.id.btn_linkedin_signin),
                view.findViewById(R.id.txt_term_of_service_signin)
        };
        for (View v: navigationViews)
        {
            v.setOnClickListener(onClickListener);
        }

        return view;
    }
}
