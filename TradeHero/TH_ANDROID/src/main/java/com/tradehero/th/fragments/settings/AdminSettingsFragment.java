package com.ayondo.academy.fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.OnBoardActivity;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.fragments.competition.CompetitionPreseasonDialogFragment;
import com.ayondo.academy.fragments.fxonboard.FxOnBoardDialogFragment;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.push.PushConstants;
import com.ayondo.academy.network.ServerEndpoint;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.TimberOnErrorAction1;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.ayondo.academy.utils.AlertDialogRxUtil;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AdminSettingsFragment extends BasePreferenceFragment
{
    private static final CharSequence KEY_USER_INFO = "user_info";
    private static final CharSequence KEY_SERVER_ENDPOINT = "server_endpoint";
    private static final CharSequence KEY_SEND_FAKE_PUSH = "send_fake_push";
    private static final CharSequence KEY_SEND_FAKE_PUSH_ACTION = "send_fake_push_action";
    private static final CharSequence KEY_DAILY_TEST_SCREEN = "show_daily_quest_test_screen";
    private static final CharSequence KEY_ACHIEVEMENT_TEST_SCREEN = "show_achievement_test_screen";
    private static final CharSequence KEY_XP_TEST_SCREEN = "show_xp_test_screen";
    private static final CharSequence KEY_TYPOGRAPHY_SCREEN = "show_typography_examples";
    private static final CharSequence KEY_PRESEASON = "show_preseason_dialog";
    private static final CharSequence KEY_KCHART = "show_kchart_examples";

    @Inject @ServerEndpoint StringPreference serverEndpointPreference;
    @Inject THApp app;
    @Inject UserProfileCacheRx userProfileCache;
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
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                            {
                                Preference pref = AdminSettingsFragment.this.findPreference(KEY_USER_INFO);
                                pref.setSummary(getString(R.string.admin_setting_user_info, pair.second.displayName, pair.first.key));
                            }
                        },
                        new ToastOnErrorAction1()));
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
                    return AdminSettingsFragment.this.onServerEndpointChanged((String) newValue);
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
                return AdminSettingsFragment.this.askForNotificationId();
            }
        });

        Preference sendFakePushAction = findPreference(KEY_SEND_FAKE_PUSH_ACTION);
        sendFakePushAction.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                return AdminSettingsFragment.this.sendFakeAction();
            }
        });

        Preference showReviewDialog = findPreference("show_review_dialog");
        showReviewDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                FragmentActivity activity = (FragmentActivity) currentActivity.get();
                AskForReviewDialogFragment.showReviewDialog(activity.getSupportFragmentManager());
                return true;
            }
        });

        Preference showInviteDialog = findPreference("show_invite_dialog");
        showInviteDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                FragmentActivity activity = (FragmentActivity) currentActivity.get();
                AskForInviteDialogFragment.showInviteDialog(activity.getSupportFragmentManager());
                return true;
            }
        });

        Preference showOnBoardDialog = findPreference("show_onBoard_dialog");
        showOnBoardDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                navigator.get().launchActivity(OnBoardActivity.class);
                return true;
            }
        });

        Preference showFxOnBoardDialog = findPreference("show_fx_onBoard_dialog");
        showFxOnBoardDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                FxOnBoardDialogFragment.showOnBoardDialog(AdminSettingsFragment.this.getActivity().getSupportFragmentManager());
                return true;
            }
        });

        Preference showTestDaily = findPreference(KEY_DAILY_TEST_SCREEN);
        final Class questListFragmentClass = AdminSettingsFragmentUtil.getQuestListTestingFragmentClass();
        showTestDaily.setEnabled(questListFragmentClass != null);
        if (questListFragmentClass != null)
        {
            showTestDaily.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    navigator.get().pushFragment(questListFragmentClass);
                    return true;
                }
            });
        }

        Preference showTestAchievement = findPreference(KEY_ACHIEVEMENT_TEST_SCREEN);
        final Class achievementListFragmentClass = AdminSettingsFragmentUtil.getAchievementListTestingFragmentClass();
        showTestAchievement.setEnabled(achievementListFragmentClass != null);
        if (achievementListFragmentClass != null)
        {
            showTestAchievement.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    navigator.get().pushFragment(achievementListFragmentClass);
                    return true;
                }
            });
        }

        Preference showXPTest = findPreference(KEY_XP_TEST_SCREEN);
        final Class xpTestingClass = AdminSettingsFragmentUtil.getXpTestingFragmentClass();
        showXPTest.setEnabled(xpTestingClass != null);
        if (xpTestingClass != null)
        {
            showXPTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    navigator.get().pushFragment(xpTestingClass);
                    return true;
                }
            });
        }

        Preference showTypography = findPreference(KEY_TYPOGRAPHY_SCREEN);
        final Class typographyTestingClass = AdminSettingsFragmentUtil.getTypographyExampleFragment();
        showTypography.setEnabled(typographyTestingClass != null);
        if (typographyTestingClass != null)
        {
            showTypography.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    navigator.get().pushFragment(typographyTestingClass);
                    return true;
                }
            });
        }

        Preference showPreseason = findPreference(KEY_PRESEASON);
        showPreseason.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {

                CompetitionPreseasonDialogFragment dialog = CompetitionPreseasonDialogFragment.newInstance(new ProviderId(24));
                dialog.show(AdminSettingsFragment.this.getActivity().getSupportFragmentManager(), CompetitionPreseasonDialogFragment.TAG);
                return true;
            }
        });

        Preference showKChart = findPreference(KEY_KCHART);
        final Class kChartExampleFragment = AdminSettingsFragmentUtil.getKChartExampleFragment();
        showKChart.setEnabled(kChartExampleFragment != null);
        if (kChartExampleFragment != null)
        {
            showKChart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    navigator.get().pushFragment(kChartExampleFragment);
                    return true;
                }
            });
        }
    }

    private boolean askForNotificationId()
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.debug_ask_for_notification_id, null);
        final EditText input = (EditText) view.findViewById(R.id.pushNotification);

        AlertDialogRxUtil.build(getActivity())
                .setView(view)
                .setPositiveButton(R.string.ok)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .build()
                .subscribe(
                        new Action1<OnDialogClickEvent>()
                        {
                            @Override public void call(OnDialogClickEvent event)
                            {
                                if (event.isPositive())
                                {
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
                                }
                            }
                        },
                        new TimberOnErrorAction1("Failed to ask for notificationId"));
        return true;
    }

    private void sendFakePushNotification(int notificationId)
    {
        Intent fakeIntent = new Intent();
        fakeIntent.putExtra(PushConstants.KEY_PUSH_ID, String.valueOf(notificationId));
        THToast.show("TODO");
    }

    private boolean sendFakeAction()
    {
        FakePushNotificationUtil.showDialogAndSend(getActivity());
        return true;
    }

    private boolean onServerEndpointChanged(String serverEndpoint)
    {
        serverEndpointPreference.set(serverEndpoint);

        app.restartActivity(DashboardActivity.class);
        return false;
    }
}
