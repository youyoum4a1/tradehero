package com.tradehero.th.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.fragments.authentication.AuthenticationFragment;

/** Created with IntelliJ IDEA. User: tho Date: 8/21/13 Time: 11:23 AM Copyright (c) TradeHero */
public class TwitterEmailFragment extends AuthenticationFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_twitter_email, container, false);
        view.findViewById(R.id.authentication_twitter_email_button)
                .setOnClickListener(onClickListener);
        return view;
    }
}
