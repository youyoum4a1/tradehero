package com.tradehero.th.fragments.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.fragments.live.LiveCallToActionFragment;

public class LiveFragmentUtil
{
    @Bind(R.id.live_fragment_container) FrameLayout liveFragmentContainer;
    @Bind(R.id.pager) ViewPager pager;
    Fragment fragment;
    @Nullable private LiveCallToActionFragment callToActionFragment;

    public LiveFragmentUtil(Fragment f, View view)
    {
        fragment = f;
        ButterKnife.bind(this, view);
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

    public void setCallToAction(boolean isLive)
    {
        if (isLive)
        {
            setCallToActionVisible();
        }
        else
        {
            setCallToActionGone();
        }
    }

    private void setCallToActionVisible()
    {
        liveFragmentContainer.setVisibility(View.VISIBLE);
        if (callToActionFragment == null)
        {
            callToActionFragment = new LiveCallToActionFragment();
            callToActionFragment.setArguments(fragment.getArguments());
        }

        if (!callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().replace(R.id.live_fragment_container, callToActionFragment).commit();
            pager.setVisibility(View.GONE);
        }
    }

    private void setCallToActionGone()
    {
        if (callToActionFragment != null && callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().remove(callToActionFragment).commit();
        }
        liveFragmentContainer.setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);
    }

    public void onDestroy()
    {
        fragment = null;
    }

    public void onDestroyView()
    {
        ButterKnife.unbind(this);
    }
}
