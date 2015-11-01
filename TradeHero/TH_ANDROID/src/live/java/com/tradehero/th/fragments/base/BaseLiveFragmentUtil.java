package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.ConnectAccountActivity;
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.activities.LiveActivityUtil;
import com.tradehero.th.activities.LiveLoginActivity;
import com.tradehero.th.activities.SignUpLiveActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.persistence.prefs.IsLiveLogIn;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.persistence.prefs.LiveAvailability;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.widget.GoLiveWidget;
import javax.inject.Inject;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.functions.Action1;
import rx.functions.Func2;

public class BaseLiveFragmentUtil
{
    public static final int CODE_PROMPT = 1;

    Fragment fragment;

    @Inject DashboardNavigator navigator;
    @Inject FastFillUtil fastFill;
    @Inject @LiveAvailability BooleanPreference liveAvailability;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    @Inject @IsLiveLogIn BooleanPreference isLiveLogIn;
    @Inject LiveActivityUtil liveActivityUtil;
    @Inject LiveBrokerSituationPreference liveBrokerSituationPreference;

    @Nullable @Bind(R.id.go_live_widget) GoLiveWidget liveWidget;

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

        if (liveWidget != null)
        {
            setUpLiveWidgetBanner(f);
        }
    }

    private void setUpLiveWidgetBanner(Fragment f)
    {
        if (isLiveTrading.get() || isLiveLogIn.get())
        {
            liveWidget.setVisibility(View.GONE);
        }

        if (liveBrokerSituationPreference.get().kycForm != null)
        {
            liveWidget.updateButtonImage(R.drawable.live_banner_on_going_user);
        }

        Observable.combineLatest(
                liveWidget.getGoLiveButtonClickedObservable(),
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

        liveWidget.getDismissLiveWidgetButtonClickedObservable()
                .subscribe(new Action1<OnClickEvent>()
                {
                    @Override public void call(OnClickEvent onClickEvent)
                    {
                        liveAvailability.set(false);
                        liveWidget.setVisibility(View.GONE);
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

    public void onDestroyView()
    {
        ButterKnife.unbind(this);
        fragment = null;
    }

    public void onResume()
    {
        //Do nothing
    }

    public void launchPrompt()
    {
        fragment.startActivityForResult(new Intent(fragment.getActivity(), IdentityPromptActivity.class), CODE_PROMPT);
    }

    public void launchLiveLogin()
    {
        fragment.startActivityForResult(new Intent(fragment.getActivity(), LiveLoginActivity.class), CODE_PROMPT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CODE_PROMPT && resultCode == Activity.RESULT_CANCELED)
        {
            liveActivityUtil.switchLive(false);
        }

        if (liveWidget != null)
        {
            liveWidget.setVisibility(View.GONE);
        }
    }

    public void setLiveWidgetTranslationY(float y)
    {
        if (liveWidget != null)
        {
            liveWidget.setTranslationY(y);
        }
    }
}
