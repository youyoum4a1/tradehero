package com.tradehero.th.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.widget.LiveSwitcher;
import javax.inject.Inject;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class LiveActivityUtil
{
    private DashboardActivity dashboardActivity;
    private CompositeSubscription onDestroyOptionsMenuSubscriptions;

    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    private PublishSubject<Boolean> isTradingLivePublishSubject;

    public LiveActivityUtil(DashboardActivity dashboardActivity)
    {
        this.dashboardActivity = dashboardActivity;
        isTradingLivePublishSubject = PublishSubject.create();
        HierarchyInjector.inject(dashboardActivity, this);
    }

    public void onCreateOptionsMenu( Menu menu)
    {
        if (onDestroyOptionsMenuSubscriptions != null)
        {
            onDestroyOptionsMenuSubscriptions.unsubscribe();
            onDestroyOptionsMenuSubscriptions.clear();
            onDestroyOptionsMenuSubscriptions = null;
        }
        onDestroyOptionsMenuSubscriptions = new CompositeSubscription();

        dashboardActivity.getMenuInflater().inflate(R.menu.live_switch_menu, menu);
        MenuItem item = menu.findItem(R.id.switch_live);
        final LiveSwitcher liveSwitcher = (LiveSwitcher) item.getActionView();

        onDestroyOptionsMenuSubscriptions.add(liveSwitcher.getSwitchObservable().subscribe(isTradingLivePublishSubject));
        isTradingLivePublishSubject
                .doOnNext(new Action1<Boolean>()
                {
                    @Override public void call(Boolean isLive)
                    {
                        //Every-time a change happened.
                        isLiveTrading.set(isLive);
                    }
                })
                .subscribe(new Action1<Boolean>()
                {
                    @Override public void call(Boolean isLive)
                    {
                        liveSwitcher.setIsLive(isLive);
                        onLiveTradingChanged(isLive);
                    }
                });
        liveSwitcher.setIsLive(isLiveTrading.get());
        for (Fragment f : dashboardActivity.getSupportFragmentManager().getFragments())
        {
            if (f instanceof BaseFragment && f.isVisible() && ((BaseFragment) f).shouldShowLiveTradingToggle())
            {
                item.setVisible(true);
                break;
            }
        }
    }

    private void onLiveTradingChanged(Boolean isLive)
    {
        dashboardActivity.getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(dashboardActivity.getResources().getColor(isLive ? R.color.tradehero_red : R.color.tradehero_blue)));

        //Specific to this activity?
        dashboardActivity.drawerLayout.setStatusBarBackgroundColor(
                dashboardActivity.getResources().getColor(isLive ? R.color.tradehero_red_status_bar : R.color.tradehero_blue_status_bar));

        for (int i = 0; i < dashboardActivity.dashboardTabHost.getTabWidget().getChildCount(); i++)
        {
            dashboardActivity.dashboardTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(
                            isLive ? R.drawable.tradehero_bottom_tab_indicator_red : R.drawable.tradehero_bottom_tab_indicator);
        }
        for (Fragment f : dashboardActivity.getSupportFragmentManager().getFragments())
        {
            if (f instanceof BaseFragment && f.isVisible())
            {
                ((BaseFragment) f).onLiveTradingChanged(isLive);
            }
        }
    }

    public void onDestroy()
    {
        if (onDestroyOptionsMenuSubscriptions != null)
        {
            onDestroyOptionsMenuSubscriptions.unsubscribe();
            onDestroyOptionsMenuSubscriptions.clear();
        }
        this.isTradingLivePublishSubject = null;
        this.dashboardActivity = null;
    }
}
