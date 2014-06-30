package com.tradehero.th.fragments.updatecenter.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.thoj.route.Routable;
import com.tradehero.thm.R;
import com.tradehero.th.fragments.base.DashboardFragment;

@Routable("notifications")
public class NotificationsCenterFragment extends DashboardFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.notifications_center, container, false);
        return view;
    }
}
