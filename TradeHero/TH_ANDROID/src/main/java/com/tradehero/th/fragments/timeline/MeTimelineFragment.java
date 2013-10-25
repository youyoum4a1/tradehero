package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 3:35 PM Copyright (c) TradeHero */
public class MeTimelineFragment extends TimelineFragment
{
    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (currentUserBase != null)
        {
            getArguments().putInt(UserBaseKey.BUNDLE_KEY_KEY, currentUserBase.id);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
