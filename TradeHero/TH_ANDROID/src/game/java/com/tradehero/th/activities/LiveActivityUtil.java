package com.tradehero.th.activities;

import android.view.Menu;
import com.tradehero.th.fragments.trending.TileType;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;

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
