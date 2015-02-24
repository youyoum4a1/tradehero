package com.tradehero.th.models.market;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.market.MarketRegion;
import timber.log.Timber;

public class MarketRegionDisplayUtil
{
    @ColorRes public static int getColorRes(@NonNull MarketRegion region)
    {
        switch (region)
        {
            case NORTH_AMERICA:
                return R.color.market_region_north_america;
            case EUROPE:
                return R.color.market_region_europe;
            case INDIA:
                return R.color.market_region_india;
            case SOUTH_EAST_ASIA:
                return R.color.market_region_south_east_asia;
            case EAST_ASIA:
                return R.color.market_region_east_asia;
            case AUSTRALIA:
                return R.color.market_region_australia;
        }
        Timber.e(new Exception(""), "Unknown MarketRegion.%s", region);
        return R.color.market_region_unknown;
    }

    @DrawableRes public static int getBgDrawableRes(@NonNull MarketRegion region)
    {
        switch (region)
        {
            case NORTH_AMERICA:
                return R.drawable.basic_dark_blue_selector;
            case EUROPE:
                return R.drawable.basic_green_selector;
            case INDIA:
                return R.drawable.basic_orange_selector;
            case SOUTH_EAST_ASIA:
                return R.drawable.basic_purple_selector;
            case EAST_ASIA:
                return R.drawable.basic_red_selector;
            case AUSTRALIA:
                return R.drawable.basic_yellow_selector;
        }
        Timber.e(new Exception(""), "Unknown MarketRegion.%s", region);
        return R.drawable.basic_light_blue_selector;
    }

    @StringRes public static int getLabelRes(@NonNull MarketRegion region)
    {
        switch (region)
        {
            case NORTH_AMERICA:
                return R.string.market_region_north_america;
            case EUROPE:
                return R.string.market_region_europe;
            case INDIA:
                return R.string.market_region_india;
            case SOUTH_EAST_ASIA:
                return R.string.market_region_south_east_asia;
            case EAST_ASIA:
                return R.string.market_region_east_asia;
            case AUSTRALIA:
                return R.string.market_region_australia;
        }
        Timber.e(new Exception(""), "Unknown MarketRegion.%s", region);
        return R.string.market_region_unknown;
    }
}
