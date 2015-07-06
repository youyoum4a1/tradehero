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
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.activities.SignUpLiveActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.live.LiveCallToActionFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.persistence.prefs.ShowCallToActionFragmentPreference;
import com.tradehero.th.widget.GoLiveWidget;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func2;

public class LiveFragmentUtil
{
    @Bind(R.id.live_fragment_container) FrameLayout liveFragmentContainer;
    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.live_button_go_live) GoLiveWidget liveWidget;
    @Nullable private LiveCallToActionFragment callToActionFragment;
    Fragment fragment;

    @Inject @ShowCallToActionFragmentPreference BooleanPreference showCallToActionFragment;
    @Inject DashboardNavigator navigator;
    @Inject FastFillUtil fastFill;

    private Subscription laterClickedSubscription;

    public LiveFragmentUtil(Fragment f, View view)
    {
        fragment = f;
        ButterKnife.bind(this, view);
        HierarchyInjector.inject(f.getActivity(), this);

        Observable.combineLatest(
                ViewObservable.clicks(liveWidget),
                fastFill.isAvailable(f.getActivity()),
                new Func2<OnClickEvent, Boolean, Boolean>()
                {
                    @Override public Boolean call(OnClickEvent onClickEvent, Boolean fastFillAvailable)
                    {
                        return fastFillAvailable;
                    }
                })
                .subscribe(new Action1<Boolean>()
                {
                    @Override public void call(Boolean fastFillAvailable)
                    {
                        navigator.launchActivity(fastFillAvailable
                                ? IdentityPromptActivity.class
                                : SignUpLiveActivity.class);
                    }
                });

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
        liveWidget.setVisibility(View.VISIBLE);
    }

    private void showCallToActionBubbleGone()
    {
        liveWidget.setVisibility(View.GONE);
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
