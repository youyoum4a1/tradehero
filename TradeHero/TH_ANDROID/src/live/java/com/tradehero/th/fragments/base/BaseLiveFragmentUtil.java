package com.tradehero.th.fragments.base;

import android.support.v4.app.Fragment;
import android.view.View;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.persistence.prefs.LiveAvailability;
import javax.inject.Inject;

public class BaseLiveFragmentUtil
{
    Fragment fragment;

    @Inject DashboardNavigator navigator;
    @Inject FastFillUtil fastFill;
    @Inject @LiveAvailability BooleanPreference liveAvailability;

    public static BaseLiveFragmentUtil createFor(Fragment fragment, View view)
    {
        return new BaseLiveFragmentUtil(fragment, view);
    }

    //Be careful of cyclic dependency. Improve this! most likely create an empty constructor and a new method onViewCreated(), pass the fragment and view through those method.
    protected BaseLiveFragmentUtil(Fragment f, View view)
    {
        fragment = f;
        ButterKnife.bind(this, view);
        HierarchyInjector.inject(f.getActivity(), this);
    }

    public static void setDarkBackgroundColor(boolean isLive, View... views)
    {
        for (View v : views)
        {
            v.setBackgroundColor(v.getContext().getResources().getColor(isLive ? R.color.tradehero_dark_red : R.color.tradehero_dark_blue));
        }
    }

    public static void setBackgroundColor(boolean isLive, View... views)
    {
        for (View v : views)
        {
            v.setBackgroundColor(v.getContext().getResources().getColor(isLive ? R.color.tradehero_red : R.color.tradehero_blue));
        }
    }

    public static void setSelectableBackground(boolean isLive, View... views)
    {
        for (View v : views)
        {
            v.setBackgroundResource(isLive ? R.drawable.basic_red_selector : R.drawable.basic_blue_selector);
        }
    }

    public void onDestroyView()
    {
        ButterKnife.unbind(this);
        fragment = null;
    }

    public void onResume()
    {
        //Do nothing
    }
}
