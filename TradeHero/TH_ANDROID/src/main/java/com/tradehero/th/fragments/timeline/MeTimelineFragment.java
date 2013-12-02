package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 3:35 PM Copyright (c) TradeHero */
public class MeTimelineFragment extends TimelineFragment
{
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (currentUserBaseKeyHolder != null)
        {
            getArguments().putInt(BUNDLE_KEY_SHOW_USER_ID, currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
