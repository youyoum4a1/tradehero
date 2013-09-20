package com.tradehero.th.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.base.THUser;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 3:35 PM Copyright (c) TradeHero */
public class MeTimelineFragment extends TimelineFragment
{

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.profile_screen, container, false);
        profile = THUser.getCurrentUser();
        if (profile != null)
        {
            profileId = profile.id;
            initView(view);
        }
        return view;
    }
}
