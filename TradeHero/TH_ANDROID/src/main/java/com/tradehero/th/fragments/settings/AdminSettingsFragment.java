package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.view.View;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.base.Application;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/19/14 Time: 1:08 PM Copyright (c) TradeHero
 */
public class AdminSettingsFragment extends PreferenceFragment
{
    @Inject @ServerEndpoint StringPreference serverEndpointPreference;
    @Inject Application app;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.admin_settings);

        DaggerUtils.inject(this);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        initPreferenceClickHandlers();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initPreferenceClickHandlers()
    {
        ListPreference serverEndpointPreference = (ListPreference) findPreference("server_endpoint");
        if (serverEndpointPreference != null)
        {
            serverEndpointPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return onServerEndpointChanged((String) newValue);
                }
            });
        }
    }

    private boolean onServerEndpointChanged(String serverEndpoint)
    {
        serverEndpointPreference.set(serverEndpoint);

        app.restartActivity(DashboardActivity.class);
        return false;
    }
}
