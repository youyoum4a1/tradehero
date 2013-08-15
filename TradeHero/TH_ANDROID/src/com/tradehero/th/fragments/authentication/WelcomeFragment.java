package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import com.tradehero.th.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeFragment extends AuthenticationFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.welcome_screen, container, false);

        Button btnNewUser = (Button) view.findViewById(R.id.btn_signup);
        btnNewUser.setOnClickListener(onClickListener);

        Button btnSignIn = (Button)view.findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(onClickListener);

        return view;
    }
}
