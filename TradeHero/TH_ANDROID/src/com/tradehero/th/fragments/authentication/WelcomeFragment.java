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
        View view = inflater.inflate(R.layout.authentication_welcome_screen, container, false);

        view.findViewById(R.id.btn_signup).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_signin).setOnClickListener(onClickListener);

        getSherlockActivity().getSupportActionBar().hide();
        return view;
    }

    @Override public void onResume()
    {
        getSherlockActivity().getSupportActionBar().hide();
        super.onResume();
    }
}
