package com.tradehero.th.fragments.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.fragments.ForTypographyFragment;
import com.tradehero.th.fragments.achievement.ForAchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.ForQuestListTestingFragment;
import com.tradehero.th.fragments.level.ForXpTestingFragment;
import com.tradehero.th.fragments.onboarding.OnBoardDialogFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Provider;

public class AdminSettingsFragment extends DashboardPreferenceFragment
{
    private static final CharSequence KEY_USER_INFO = "user_info";
    private static final CharSequence KEY_SERVER_ENDPOINT = "server_endpoint";
    private static final CharSequence KEY_SEND_FAKE_PUSH = "send_fake_push";
    private static final CharSequence KEY_DAILY_TEST_SCREEN = "show_daily_quest_test_screen";
    private static final CharSequence KEY_ACHIEVEMENT_TEST_SCREEN = "show_achievement_test_screen";
    private static final CharSequence KEY_XP_TEST_SCREEN = "show_xp_test_screen";
    private static final CharSequence KEY_TYPOGRAPHY_SCREEN = "show_typography_examples";

    @Inject @ServerEndpoint StringPreference serverEndpointPreference;
    @Inject THApp app;
    @Inject Provider<NotificationOpenedHandler> notificationOpenedHandler;
    @Inject @ForQuestListTestingFragment Provider<Class> questListTestingFragmentClassProvider;
    @Inject @ForAchievementListTestingFragment Provider<Class> achievementListTestingFragmentClassProvider;
    @Inject @ForXpTestingFragment Provider<Class> xpTestingFragmentClassProvider;
    @Inject @ForTypographyFragment Provider<Class> typographyFragmentClassProvider;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Provider<Activity> currentActivity;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        HierarchyInjector.inject(this);
        addPreferencesFromResource(R.xml.admin_settings);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        initPreferenceClickHandlers();
        initDefaultValue();
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(dashboardBottomTabsScrollListener.get());
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
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(getString(R.string.admin_setting));
        }
    }

    private void initPreferenceClickHandlers()
    {
        ListPreference serverEndpointListPreference = (ListPreference) findPreference(KEY_SERVER_ENDPOINT);
        if (serverEndpointPreference != null)
        {
            serverEndpointListPreference.setOnPreferenceChangeListener((preference, newValue) -> onServerEndpointChanged((String) newValue));

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
        sendFakePush.setOnPreferenceClickListener(preference -> askForNotificationId());

        Preference showReviewDialog = findPreference("show_review_dialog");
        showReviewDialog.setOnPreferenceClickListener(preference -> {
            FragmentActivity activity = (FragmentActivity) currentActivity.get();
            AskForReviewDialogFragment.showReviewDialog(activity.getFragmentManager());
            return true;
        });

        Preference showInviteDialog = findPreference("show_invite_dialog");
        showInviteDialog.setOnPreferenceClickListener(preference -> {
            FragmentActivity activity = (FragmentActivity) currentActivity.get();
            AskForInviteDialogFragment.showInviteDialog(activity.getFragmentManager());
            return true;
        });

        Preference showOnBoardDialog = findPreference("show_onBoard_dialog");
        showOnBoardDialog.setOnPreferenceClickListener(preference -> {
            // FragmentActivity activityversion.properties = (FragmentActivity) currentActivityHolder.getCurrentActivity();
            OnBoardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
            return true;
        });

        Preference showTestDaily = findPreference(KEY_DAILY_TEST_SCREEN);
        showTestDaily.setEnabled(questListTestingFragmentClassProvider.get() != null);
        showTestDaily.setOnPreferenceClickListener(preference -> {
            navigator.get().pushFragment(questListTestingFragmentClassProvider.get());
            return true;
        });

        Preference showTestAchievement = findPreference(KEY_ACHIEVEMENT_TEST_SCREEN);
        showTestAchievement.setEnabled(achievementListTestingFragmentClassProvider.get() != null);
        showTestAchievement.setOnPreferenceClickListener(preference -> {
            navigator.get().pushFragment(achievementListTestingFragmentClassProvider.get());
            return true;
        });

        Preference showXPTest = findPreference(KEY_XP_TEST_SCREEN);
        showXPTest.setEnabled(xpTestingFragmentClassProvider.get() != null);
        showXPTest.setOnPreferenceClickListener(preference -> {
            navigator.get().pushFragment(xpTestingFragmentClassProvider.get());
            return true;
        });

        Preference showTypography = findPreference(KEY_TYPOGRAPHY_SCREEN);
        showTypography.setEnabled(typographyFragmentClassProvider.get() != null);
        showTypography.setOnPreferenceClickListener(preference -> {
            navigator.get().pushFragment(typographyFragmentClassProvider.get());
            return true;
        });
    }

    private boolean askForNotificationId()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.debug_ask_for_notification_id, null);
        final EditText input = (EditText) view.findViewById(R.id.pushNotification);
        alert.setView(view);
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            Editable value = input.getText();
            int notificationId = 0;
            try
            {
                notificationId = Integer.parseInt(value.toString());
            } catch (NumberFormatException ex)
            {
                THToast.show("Not a number");
            }
            sendFakePushNotification(notificationId);
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
