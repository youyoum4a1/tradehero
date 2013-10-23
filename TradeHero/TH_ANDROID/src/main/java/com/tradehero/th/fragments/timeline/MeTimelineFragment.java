package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.THUser;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 3:35 PM Copyright (c) TradeHero */
public class MeTimelineFragment extends TimelineFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        UserBaseDTO u = THUser.getCurrentUserBase();
        if (u != null)
        {
            getArguments().putInt(BUNDLE_KEY_USER_ID, u.id);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
