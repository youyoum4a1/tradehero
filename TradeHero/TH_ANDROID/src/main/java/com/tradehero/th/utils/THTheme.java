package com.tradehero.th.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import javax.inject.Inject;

public class THTheme {

    @Inject @IsLiveTrading BooleanPreference isLiveTrading;

    private Context context;
    private THThemeSetting setting;

    public THTheme(Context context, THThemeSetting setting) {
        this.context = context;
        this.setting = setting;
        HierarchyInjector.inject(context, this);
    }

    public THThemeSetting getSetting() {
        return setting;
    }

    public int mainColor() {

        if (isLiveTrading.get()) {
            //switch (setting) {
            //    case RABBIT_SETTING:
            //        return ContextCompat.getColor(context, R.color.rabbit_color);
            //    default:
            //        return ContextCompat.getColor(context, R.color.tradehero_blue);
            //}
            return ContextCompat.getColor(context, R.color.live_color);
        }

        switch (setting) {
            case RABBIT_SETTING:
                return ContextCompat.getColor(context, R.color.rabbit_color);
            default:
                return ContextCompat.getColor(context, R.color.tradehero_blue);
        }
    }

    public int statusBarColor() {
        if (isLiveTrading.get()) {
            //switch (setting) {
            //    case RABBIT_SETTING:
            //        return ContextCompat.getColor(context, R.color.rabbit_color);
            //    default:
            //        return ContextCompat.getColor(context, R.color.tradehero_blue);
            //}
            return ContextCompat.getColor(context, R.color.live_status_bar_700);
        }

        switch (setting) {
            case RABBIT_SETTING:
                return ContextCompat.getColor(context, R.color.rabbit_status_bar_700);
            default:
                return ContextCompat.getColor(context, R.color.tradehero_blue_status_bar_700);
        }
    }

    public int tabBarColor() {
        if (isLiveTrading.get()) {
            //switch (setting) {
            //    case RABBIT_SETTING:
            //        return ContextCompat.getColor(context, R.color.rabbit_color);
            //    default:
            //        return ContextCompat.getColor(context, R.color.tradehero_blue);
            //}
            return R.drawable.live_bottom_tab_indicator;
        }

        switch (setting) {
            case RABBIT_SETTING:
                return R.drawable.rabbit_bottom_tab_indicator;
            default:
                return R.drawable.tradehero_bottom_tab_indicator;
        }
    }
}
