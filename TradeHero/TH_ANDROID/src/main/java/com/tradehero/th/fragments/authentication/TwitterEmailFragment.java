package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;

public class TwitterEmailFragment extends Fragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_twitter_email, container, false);
        // FIXME
        //view.findViewById(R.id.authentication_twitter_email_button)
        //        .setOnClickListener(onClickListener);
        return view;
    }
}
