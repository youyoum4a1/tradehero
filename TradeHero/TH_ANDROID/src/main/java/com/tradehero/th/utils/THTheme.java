package com.tradehero.th.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.tradehero.th.R;

public class THTheme {

    private Context context;

    private THThemeSetting setting;

    public THTheme(Context context, THThemeSetting setting) {
        this.context = context;
        this.setting = setting;
    }

    public THThemeSetting getSetting() {
        return setting;
    }

    public int mainColor() {
        switch (setting) {
            case RABBIT_SETTING:
                return ContextCompat.getColor(context, R.color.rabbit_color);
            default:
                return ContextCompat.getColor(context, R.color.tradehero_blue);
        }
    }

    public int statusBarColor() {
        switch (setting) {
            case RABBIT_SETTING:
                return ContextCompat.getColor(context, R.color.rabbit_status_bar_700);
            default:
                return ContextCompat.getColor(context, R.color.tradehero_blue_status_bar_700);
        }
    }

    public int tabBarColor() {
        switch (setting) {
            case RABBIT_SETTING:
                return R.drawable.rabbit_bottom_tab_indicator;
            default:
                return R.drawable.tradehero_bottom_tab_indicator;
        }
    }
}
