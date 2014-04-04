package com.tradehero.th.fragments.updatecenter.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by tho on 14-4-3.
 */
public class NotificationsCenterFragment extends DashboardFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.notifications_center, container, false);
        return view;
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
