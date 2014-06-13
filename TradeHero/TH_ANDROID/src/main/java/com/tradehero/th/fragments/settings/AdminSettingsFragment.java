package com.tradehero.th.fragments.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import javax.inject.Provider;

public class AdminSettingsFragment extends DashboardPreferenceFragment
{
    private static final CharSequence KEY_USER_INFO = "user_info";
    private static final CharSequence KEY_SERVER_ENDPOINT = "server_endpoint";
    private static final CharSequence KEY_SEND_FAKE_PUSH = "send_fake_push";

    @Inject @ServerEndpoint StringPreference serverEndpointPreference;
    @Inject Application app;
    @Inject Provider<NotificationOpenedHandler> notificationOpenedHandler;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;

    @Inject AlertDialogUtil alertDialogUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        DaggerUtils.inject(this);
        addPreferencesFromResource(R.xml.admin_settings);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        initPreferenceClickHandlers();
        initDefaultValue();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initDefaultValue()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            Preference pref = findPreference(KEY_USER_INFO);
            pref.setSummary(getString(R.string.admin_setting_user_info, userProfileDTO.displayName, userProfileDTO.id));
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.admin_setting));
    }

    private void initPreferenceClickHandlers()
    {
        ListPreference serverEndpointListPreference = (ListPreference) findPreference(KEY_SERVER_ENDPOINT);
        if (serverEndpointPreference != null)
        {
            serverEndpointListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return onServerEndpointChanged((String) newValue);
                }
            });

            if (serverEndpointPreference != null)
            {
                int selectedIndex = serverEndpointListPreference.findIndexOfValue(serverEndpointPreference.get());
                CharSequence[] entries = serverEndpointListPreference.getEntries();
                if (entries != null && selectedIndex < entries.length)
                {
                    serverEndpointListPreference.setTitle(getString(R.string.current_endpoint) + entries[selectedIndex]);
                }
                else
                {
                    serverEndpointListPreference.setTitle(getString(R.string.select_endpoint));
                }
            }
        }

        Preference sendFakePush = findPreference(KEY_SEND_FAKE_PUSH);
        sendFakePush.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                return askForNotificationId();
            }
        });
    }

    private boolean askForNotificationId()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.debug_ask_for_notification_id, null);
        final EditText input = (EditText) view.findViewById(R.id.pushNotification);
        alert.setView(view);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Editable value = input.getText();
                int notificationId = 0;
                try
                {
                    notificationId = Integer.parseInt(value.toString());
                }
                catch (NumberFormatException ex)
                {
                    THToast.show("Not a number");
                }
                sendFakePushNotification(notificationId);
            }
        });
        alert.show();
        return true;
    }

    private void sendFakePushNotification(int notificationId)
    {
        Intent fakeIntent = new Intent();
        fakeIntent.putExtra(PushConstants.KEY_PUSH_ID, String.valueOf(notificationId));
        notificationOpenedHandler.get().handle(fakeIntent);
    }

    private boolean onServerEndpointChanged(String serverEndpoint)
    {
        serverEndpointPreference.set(serverEndpoint);

        app.restartActivity(DashboardActivity.class);
        return false;
    }
}
