package com.ayondo.academy.activities;

import android.view.Menu;
import com.ayondo.academy.fragments.trending.TileType;
import com.ayondo.academy.utils.route.THRouter;
import com.ayondo.academy.widget.OffOnViewSwitcherEvent;

public class LiveActivityUtil
{
    public LiveActivityUtil(DashboardActivity dashboardActivity)
    {
    }

    public static Class<?> getRoutableKYC()
    {
        return LiveActivityUtil.class;
    }

    public void onCreateOptionsMenu(Menu menu)
    {
    }

    private void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
    }

    public void onDestroy()
    {
    }

    public void switchLive(boolean isLive)
    {
    }

    public void supportInvalidateOptionsMenu()
    {
    }

    public void onTrendingTileClicked(TileType tileType){

    }

    public static void registerAliases(THRouter router)
    {

    }
}
