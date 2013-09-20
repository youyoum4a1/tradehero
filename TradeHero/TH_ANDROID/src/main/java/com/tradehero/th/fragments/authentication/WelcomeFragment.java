package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import com.tradehero.th.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.auth.AuthenticationMode;

public class WelcomeFragment extends AuthenticationFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_welcome_screen, container, false);

        view.findViewById(R.id.authentication_by_sign_up_button).setOnClickListener(onClickListener);
        view.findViewById(R.id.authentication_by_sign_in_button).setOnClickListener(onClickListener);
        view.findViewById(R.id.authentication_by_sign_in_button).startAnimation(
                AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        getSherlockActivity().getSupportActionBar().hide();
        return view;
    }

    @Override public void onResume()
    {
        getSherlockActivity().getSupportActionBar().hide();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        getSherlockActivity().getSupportActionBar().show();
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.Unknown;
    }
}
