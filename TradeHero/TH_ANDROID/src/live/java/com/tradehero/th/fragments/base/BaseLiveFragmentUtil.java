package com.tradehero.th.fragments.base;

import android.support.v4.app.Fragment;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.activities.SignUpLiveActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.persistence.prefs.LiveAvailability;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.widget.GoLiveWidget;
import javax.inject.Inject;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func2;

public class BaseLiveFragmentUtil
{
    @Bind(R.id.live_button_go_live) GoLiveWidget liveWidget;
    Fragment fragment;

    @Inject DashboardNavigator navigator;
    @Inject FastFillUtil fastFill;
    @Inject @LiveAvailability BooleanPreference liveAvailability;

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
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean fastFillAvailable)
                            {
                                navigator.launchActivity(fastFillAvailable
                                        ? IdentityPromptActivity.class
                                        : SignUpLiveActivity.class);
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to liveWidget in BaseLiveFragmentUtil"));
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
        if(liveAvailability.get())
        {
            showCallToActionBubbleVisible();
        }
    }

    protected void showCallToActionBubbleVisible()
    {
        liveWidget.setVisibility(View.VISIBLE);
    }

    protected void showCallToActionBubbleGone()
    {
        liveWidget.setVisibility(View.GONE);
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

    public void setLiveWidgetTranslationY(float y)
    {
        liveWidget.setTranslationY(y);
    }

    public void setLiveWidgetVisibility(int visibility)
    {
        liveWidget.setVisibility(visibility);
    }
}
