package com.tradehero.th.utils;

import android.content.Context;

import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.utils.prefs.CurrentTheme;

import javax.inject.Inject;

public class THThemeManager {

    private static final THThemeManager manager = new THThemeManager();
    @Inject @CurrentTheme IntPreference currentTheme;

    public THThemeManager() {

    }

    public static THThemeManager getManager() {
        return manager;
    }

    public void setCurrentTheme(THTheme theme) {
        currentTheme.set(theme.getSetting().ordinal());
    }

    public THTheme getCurrentTheme(Context context) {
        int current;

        if (currentTheme != null) {
            current = currentTheme.get();
        } else {
            current = THThemeSetting.RABBIT_SETTING.ordinal();
        }

        if (THThemeSetting.values().length - 1 < current) {
            return new THTheme(context, THThemeSetting.DEFAULT_SETTING);
        }

        return new THTheme(context, THThemeSetting.values()[current]);
    }
}
