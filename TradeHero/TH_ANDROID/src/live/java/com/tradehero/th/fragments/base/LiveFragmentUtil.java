package com.tradehero.th.fragments.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.live.LiveCallToActionFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.ShowCallToActionFragmentPreference;
import javax.inject.Inject;
import rx.Subscription;
import rx.functions.Action1;

public class LiveFragmentUtil
{
    @Bind(R.id.live_fragment_container) FrameLayout liveFragmentContainer;
    @Bind(R.id.pager) ViewPager pager;
    Fragment fragment;
    @Nullable private LiveCallToActionFragment callToActionFragment;
    @Inject @ShowCallToActionFragmentPreference BooleanPreference showCallToActionFragment;
    private Subscription laterClickedSubscription;

    public LiveFragmentUtil(Fragment f, View view)
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

    public void setCallToAction(boolean isLive)
    {
        if (isLive)
        {
            if (showCallToActionFragment.get())
            {
                setCallToActionFragmentVisible();
            }
            else
            {
                showCallToActionBubbleVisible();
            }
        }
        else
        {
            if (showCallToActionFragment.get())
            {
                setCallToActionFragmentGone();
            }
            else
            {
                showCallToActionBubbleGone();
            }
        }
    }

    private void setCallToActionFragmentVisible()
    {
        liveFragmentContainer.setVisibility(View.VISIBLE);
        if (callToActionFragment == null)
        {
            callToActionFragment = new LiveCallToActionFragment();
            callToActionFragment.setArguments(fragment.getArguments());
        }

        laterClickedSubscription = callToActionFragment.getOnLaterClickedSubscribtion().subscribe(new Action1<View>()
        {
            @Override public void call(View view)
            {
                setCallToActionFragmentGone();
            }
        });

        if (!callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().replace(R.id.live_fragment_container, callToActionFragment).commit();
            pager.setVisibility(View.GONE);
        }
    }

    private void setCallToActionFragmentGone()
    {
        if (callToActionFragment != null && callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().remove(callToActionFragment).commit();
        }
        if (laterClickedSubscription != null)
        {
            laterClickedSubscription.unsubscribe();
        }
        liveFragmentContainer.setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);
        showCallToActionFragment.set(false); //Only display fragment once, the next one should be a bubble
    }

    private void showCallToActionBubbleVisible()
    {

    }

    private void showCallToActionBubbleGone()
    {

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
