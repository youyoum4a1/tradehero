package com.tradehero.th.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trending.TileType;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.widget.OffOnViewSwitcher;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class LiveActivityUtil
{
    private BaseActivity activity;
    private CompositeSubscription onDestroyOptionsMenuSubscriptions;

    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    private PublishSubject<OffOnViewSwitcherEvent> isTradingLivePublishSubject;
    private OffOnViewSwitcher liveSwitcher;

    public LiveActivityUtil(BaseActivity activity)
    {
        this.activity = activity;
        isTradingLivePublishSubject = PublishSubject.create();
        HierarchyInjector.inject(activity, this);
    }

    public void onCreateOptionsMenu(Menu menu)
    {
        if (onDestroyOptionsMenuSubscriptions != null)
        {
            onDestroyOptionsMenuSubscriptions.unsubscribe();
            onDestroyOptionsMenuSubscriptions.clear();
            onDestroyOptionsMenuSubscriptions = null;
        }
        onDestroyOptionsMenuSubscriptions = new CompositeSubscription();

        activity.getMenuInflater().inflate(R.menu.live_switch_menu, menu);
        MenuItem item = menu.findItem(R.id.switch_live);
        liveSwitcher = (OffOnViewSwitcher) item.getActionView();

        onDestroyOptionsMenuSubscriptions.add(
                Observable.merge(liveSwitcher.getSwitchObservable(),
                        isTradingLivePublishSubject
                                .startWith(new OffOnViewSwitcherEvent(false, isLiveTrading.get()))
                                .doOnNext(new Action1<OffOnViewSwitcherEvent>()
                                {
                                    @Override public void call(OffOnViewSwitcherEvent event)
                                    {
                                        liveSwitcher.setIsOn(event.isOn, false);
                                    }
                                }))
                        .doOnNext(new Action1<OffOnViewSwitcherEvent>()
                        {
                            @Override public void call(OffOnViewSwitcherEvent event)
                            {
                                //Every-time a change happened.
                                isLiveTrading.set(event.isOn);
                            }
                        })
                        .distinctUntilChanged(
                                new Func1<OffOnViewSwitcherEvent, Boolean>()
                                {
                                    @Override public Boolean call(OffOnViewSwitcherEvent event)
                                    {
                                        return event.isOn;
                                    }
                                })
                        .subscribe(
                                new Action1<OffOnViewSwitcherEvent>()
                                {
                                    @Override public void call(OffOnViewSwitcherEvent event)
                                    {
                                        onLiveTradingChanged(event);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to listen to liveSwitcher in LiveActivityUtil")));

        for (Fragment f : activity.getSupportFragmentManager().getFragments())
        {
            if (f instanceof DashboardFragment && f.isVisible() && ((DashboardFragment) f).shouldShowLiveTradingToggle())
            {
                item.setVisible(true);
                break;
            }
        }

        if (!item.isVisible() && isLiveTrading.get())
        {
            //There's no fragment that can handle live trading, disable it.
            switchLive(false);
        }
    }

    private void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        for (Fragment f : activity.getSupportFragmentManager().getFragments())
        {
            if (f instanceof DashboardFragment && f.isVisible())
            {
                ((DashboardFragment) f).onLiveTradingChanged(event.isOn);
            }
        }

        //changeBarColor(event);
    }

    private void changeBarColor(OffOnViewSwitcherEvent event)
    {
        activity.getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(activity.getResources().getColor(event.isOn ? R.color.tradehero_red : R.color.tradehero_blue)));

        //Specific to this activity?
        if (activity instanceof DashboardActivity)
        {
            DashboardActivity dashboardActivity = (DashboardActivity) activity;
            dashboardActivity.drawerLayout.setStatusBarBackgroundColor(
                    dashboardActivity.getResources().getColor(event.isOn ? R.color.tradehero_red_status_bar : R.color.tradehero_blue_status_bar));

            for (int i = 0; i < dashboardActivity.dashboardTabHost.getTabWidget().getChildCount(); i++)
            {
                dashboardActivity.dashboardTabHost.getTabWidget().getChildAt(i)
                        .setBackgroundResource(
                                event.isOn ? R.drawable.tradehero_bottom_tab_indicator_red : R.drawable.tradehero_bottom_tab_indicator);
            }
        }
    }

    public void supportInvalidateOptionsMenu()
    {
        if (onDestroyOptionsMenuSubscriptions != null)
        {
            onDestroyOptionsMenuSubscriptions.unsubscribe();
            onDestroyOptionsMenuSubscriptions.clear();
        }
        liveSwitcher = null;
    }

    public void onDestroy()
    {
        if (onDestroyOptionsMenuSubscriptions != null)
        {
            onDestroyOptionsMenuSubscriptions.unsubscribe();
            onDestroyOptionsMenuSubscriptions.clear();
        }
        liveSwitcher = null;
        this.isTradingLivePublishSubject = null;
        this.activity = null;
    }

    public void switchLive(boolean isLive)
    {
        switchLive(isLive, false);
    }

    private void switchLive(boolean isLive, boolean fromUser)
    {
        isTradingLivePublishSubject.onNext(new OffOnViewSwitcherEvent(fromUser, isLive));
    }

    public void onTrendingTileClicked(TileType tileType)
    {
        //Disable live toggling for now
        //if (tileType.equals(TileType.LiveToggle))
        //{
        //    switchLive(true, true);
        //}
    }
}
