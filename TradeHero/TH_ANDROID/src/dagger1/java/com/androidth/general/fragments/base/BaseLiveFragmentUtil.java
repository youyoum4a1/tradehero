package com.androidth.general.fragments.base;

import android.support.v4.app.Fragment;
import android.view.View;

import com.androidth.general.R;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.trending.TrendingMainFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.fastfill.FastFillUtil;
import com.androidth.general.persistence.prefs.LiveAvailability;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseLiveFragmentUtil
{

    Fragment fragment;

    @Inject DashboardNavigator navigator;
    @Inject
    FastFillUtil fastFill;
    @Inject @LiveAvailability
    BooleanPreference liveAvailability;
    private Unbinder unbinder;

    public static BaseLiveFragmentUtil createFor(Fragment fragment, View view)
    {
        if (fragment instanceof TrendingMainFragment)
        {
            return new TrendingLiveFragmentUtil(fragment, view);
        }
        return new BaseLiveFragmentUtil(fragment, view);
    }

    //Be careful of cyclic dependency. Improve this! most likely create an empty constructor and a new method onViewCreated(), pass the fragment and view through those method.
    protected BaseLiveFragmentUtil(Fragment f, View view)
    {
        fragment = f;
        unbinder = ButterKnife.bind(this, view);
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
        unbinder.unbind();
        fragment = null;
    }

    public void onResume()
    {
        //Do nothing
    }
}
