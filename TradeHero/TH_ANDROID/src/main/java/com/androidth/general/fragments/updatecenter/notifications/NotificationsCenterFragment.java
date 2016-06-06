package com.androidth.general.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.route.Routable;
import com.androidth.general.R;
import com.androidth.general.fragments.base.BaseFragment;
import javax.inject.Inject;

@Routable("notifications")
public class NotificationsCenterFragment extends BaseFragment
{
    @Inject Context doNotRemoveOrFails;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.notifications_center, container, false);
    }
}
