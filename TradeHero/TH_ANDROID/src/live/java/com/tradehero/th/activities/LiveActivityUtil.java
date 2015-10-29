package com.tradehero.th.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trending.TileType;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import com.tradehero.th.persistence.prefs.IsLiveColorRed;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.OffOnViewSwitcher;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;
import javax.inject.Inject;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class LiveActivityUtil
{
    private BaseActivity activity;
    private CompositeSubscription onDestroyOptionsMenuSubscriptions;

    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    @Inject @IsLiveColorRed BooleanPreference isLiveColorRed;

    private OffOnViewSwitcher liveSwitcher;

    public static Class<?> getRoutableKYC()
    {
        return IdentityPromptActivity.class;
    }

    public static void registerAliases(THRouter router)
    {
        router.registerAlias(IdentityPromptActivity.ROUTER_KYC_SCHEME + "ayondo",
                IdentityPromptActivity.ROUTER_KYC_SCHEME + DummyAyondoLiveServiceWrapper.AYONDO_LIVE_BROKER_ID);
    }

    public LiveActivityUtil(BaseActivity activity)
    {
        this.activity = activity;
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

        onDestroyOptionsMenuSubscriptions.add(liveSwitcher.getSwitchObservable()
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
        else
        {
            switchLive(isLiveTrading.get());
        }
    }

    private void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        for (Fragment f : activity.getSupportFragmentManager().getFragments())
        {
            if (f instanceof DashboardFragment && f.isVisible())
            {
                ((DashboardFragment) f).onLiveTradingChanged(event);
            }
        }

        changeBarColor(event);
    }

    private void changeBarColor(OffOnViewSwitcherEvent event)
    {
        int baseColorRes = isLiveColorRed.get() ? R.color.tradehero_test_red : R.color.tradehero_red;
        int statusBarColorRes = isLiveColorRed.get() ? R.color.tradehero_test_red_status_bar : R.color.tradehero_red_status_bar;
//        int bottomColorRes = isLiveColorRed.get() ? R.color.tradehero_test_red : R.drawable.tradehero_bottom_tab_indicator_red;

        activity.getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(activity.getResources().getColor(event.isOn ? baseColorRes : R.color.tradehero_blue)));

        //Specific to this activity?
        if (activity instanceof DashboardActivity)
        {
            DashboardActivity dashboardActivity = (DashboardActivity) activity;
            dashboardActivity.drawerLayout.setStatusBarBackgroundColor(
                    dashboardActivity.getResources().getColor(event.isOn ? statusBarColorRes : R.color.tradehero_blue_status_bar));

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
        this.activity = null;
    }

    public void switchLive(boolean isLive)
    {
        switchLive(isLive, false);
    }

    private void switchLive(boolean isLive, boolean fromUser)
    {
        liveSwitcher.setIsOn(isLive, fromUser);
    }

    public void onTrendingTileClicked(TileType tileType)
    {
    }
}
