package com.tradehero.th.fragments.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.SlowedAsyncTask;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorer;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.fragments.billing.PurchaseRestorerAlertUtil;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.TwitterUtils;
import com.tradehero.th.utils.VersionUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: nia Date: 17/10/13 Time: 12:38 PM To change this template use File | Settings | File Templates. */
public class SettingsFragment extends PreferenceFragment
{
    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject SessionService sessionService;
    @Inject SocialService socialService;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    protected UserProfileRetrievedMilestone currentUserProfileRetrievedMilestone;
    @Inject protected PushNotificationManager pushNotificationManager;

    @Inject protected Lazy<FacebookUtils> facebookUtils;
    @Inject protected Lazy<TwitterUtils> twitterUtils;
    @Inject protected Lazy<LinkedInUtils> linkedInUtils;

    private THIABUserInteractor userInteractor;
    @Inject protected PurchaseRestorerAlertUtil purchaseRestorerAlertUtil;

    private ProgressDialog progressDialog;
    private CheckBoxPreference facebookSharing;
    private SocialNetworkEnum currentSocialNetworkConnect;
    private CheckBoxPreference twitterSharing;
    private CheckBoxPreference linkedInSharing;
    private CheckBoxPreference pushNotification;
    private CheckBoxPreference emailNotification;
    private CheckBoxPreference pushNotificationSound;
    private CheckBoxPreference pushNotificationVibrate;

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        userInteractor = new THIABUserInteractor(getActivity(), ((DashboardActivity) getActivity()).getBillingActor(), getView().getHandler());
        userInteractor.setApplicablePortfolioId(null);
    }

    @Override public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        view.setBackgroundColor(getResources().getColor(R.color.white));

        this.currentUserProfileRetrievedMilestone = new UserProfileRetrievedMilestone(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        this.currentUserProfileRetrievedMilestone.setOnCompleteListener(new SettingsUserProfileRetrievedCompleteListener());
        this.currentUserProfileRetrievedMilestone.launch();

        if (view != null)
        {
            ListView listView = (ListView) view.findViewById(android.R.id.list);
            if (listView != null)
            {
                listView.setPadding(
                        (int) getResources().getDimension(R.dimen.setting_padding_left),
                        (int) getResources().getDimension(R.dimen.setting_padding_top),
                        (int) getResources().getDimension(R.dimen.setting_padding_right),
                        (int) getResources().getDimension(R.dimen.setting_padding_bottom));
            }
        }

        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        initPreferenceClickHandlers();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.settings);

        DaggerUtils.inject(this);
    }

    @Override public void onDestroyView()
    {
        this.currentUserProfileRetrievedMilestone.setOnCompleteListener(null);
        this.currentUserProfileRetrievedMilestone = null;
        super.onDestroyView();
    }

    private THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener createPurchaseRestorerListener()
    {
        return new THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener()
        {
            @Override
            public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed)
            {
                THLog.d(TAG, "onPurchaseRestoreFinished3");
                purchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                        getActivity(),
                        consumed,
                        reportFailed,
                        consumeFailed,
                        purchaseRestorerAlertUtil.createFailedRestoreClickListener(getActivity(), new Exception())); // TODO have a better exception
            }

            @Override public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> consumeFailed)
            {
                THLog.d(TAG, "onPurchaseRestoreFinished2");
            }

            @Override public void onPurchaseRestoreFailed(Throwable throwable)
            {
                THLog.e(TAG, "onPurchaseRestoreFailed", throwable);
                // Inform
                userInteractor.conditionalPopBillingNotAvailable();
            }
        };
    }

    private void initPreferenceClickHandlers()
    {
        Preference topBanner = findPreference(getString(R.string.key_preference_top_banner));
        topBanner.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                handleTopBannerClicked();
                return false;
            }
        });

        Preference settingFaq = findPreference(getString(R.string.settings_primary_faq));
        settingFaq.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                handleFaqClicked();
                return true;
            }
        });

        Preference settingAbout = findPreference(getString(R.string.settings_misc_about));

        if (settingAbout != null)
        {
            settingAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleAboutClicked();
                    return true;
                }
            });
        }

        Preference sendLoveBlock = findPreference(getString(R.string.settings_primary_send_love));
        if (sendLoveBlock != null)
        {
            sendLoveBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSendLoveClicked();
                    return true;
                }
            });
        }

        Preference sendFeedbackBlock = findPreference(getString(R.string.settings_primary_send_feedback));
        if (sendFeedbackBlock != null)
        {
            sendFeedbackBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSendFeedbackClicked();
                    return true;
                }
            });

            // TODO
            //sendFeedbackBlock.setOnLongClickListener(new View.OnLongClickListener()
            //{
            //    @Override public boolean onLongClick(View view)
            //    {
            //        handleSendFeedbackLongClicked();
            //        return true;
            //    }
            //});
        }

        Preference profileBlock = findPreference(getString(R.string.settings_primary_profile));
        if (profileBlock != null)
        {
            profileBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleProfileClicked();
                    return true;
                }
            });
        }

        Preference paypalBlock = findPreference(getString(R.string.settings_primary_paypal));
        if (paypalBlock != null)
        {
            paypalBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handlePaypalClicked();
                    return true;
                }
            });
        }

        Preference transactionHistoryBlock = findPreference(getString(R.string.settings_primary_transaction_history));
        if (transactionHistoryBlock != null)
        {
            transactionHistoryBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleTransactionHistoryClicked();
                    return true;
                }
            });
        }

        Preference restorePurchaseBlock = findPreference(getString(R.string.settings_primary_restore_purchases));
        if (restorePurchaseBlock != null)
        {
            restorePurchaseBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleRestorePurchaseClicked();
                    return true;
                }
            });
        }

        Preference resetHelpScreensBlock = findPreference(getString(R.string.settings_misc_reset_help_screens));
        if (resetHelpScreensBlock != null)
        {
            resetHelpScreensBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleResetHelpScreensClicked();
                    return true;
                }
            });
        }

        Preference clearCacheBlock = findPreference(getString(R.string.settings_misc_clear_cache));
        if (clearCacheBlock != null)
        {
            clearCacheBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleClearCacheClicked();
                    return true;
                }
            });
        }

        Preference signOutBlock = findPreference(getString(R.string.settings_misc_sign_out));
        if (signOutBlock != null)
        {
            signOutBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSignOutClicked();
                    return true;
                }
            });
        }

        Preference aboutBlock = findPreference(getString(R.string.settings_about));
        if (aboutBlock != null)
        {
            aboutBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleAboutClicked();
                    return true;
                }
            });
        }

        // Sharing
        facebookSharing = (CheckBoxPreference) findPreference(getString(R.string.settings_sharing_facebook));
        if (facebookSharing != null)
        {
            facebookSharing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeSharing(SocialNetworkEnum.FB, (boolean) newValue);
                }
            });
        }
        twitterSharing = (CheckBoxPreference) findPreference(getString(R.string.settings_sharing_twitter));
        if (twitterSharing != null)
        {
            twitterSharing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeSharing(SocialNetworkEnum.TW, (boolean) newValue);
                }
            });
        }
        linkedInSharing = (CheckBoxPreference) findPreference(getString(R.string.settings_sharing_linked_in));
        if (linkedInSharing != null)
        {
            linkedInSharing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeSharing(SocialNetworkEnum.LI, (boolean) newValue);
                }
            });
        }

        // notification
        pushNotification = (CheckBoxPreference) findPreference(getString(R.string.settings_notifications_push));
        if (pushNotification != null)
        {
            pushNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changePushNotification((boolean) newValue);
                }
            });
        }

        emailNotification = (CheckBoxPreference) findPreference(getString(R.string.settings_notifications_email));
        if (emailNotification != null)
        {
            emailNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeEmailNotification((boolean) newValue);
                }
            });
        }

        pushNotificationSound = (CheckBoxPreference) findPreference(getString(R.string.settings_notifications_push_alert_sound));
        if (pushNotificationSound != null)
        {
            pushNotificationSound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    pushNotificationManager.setSoundEnabled((boolean) newValue);
                    return true;
                }
            });
        }

        pushNotificationVibrate = (CheckBoxPreference) findPreference(getString(R.string.settings_notifications_push_alert_vibrate));
        if (pushNotificationVibrate != null)
        {
            pushNotificationVibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    pushNotificationManager.setVibrateEnabled((boolean) newValue);
                    return true;
                }
            });
        }

        if (this.currentUserProfileRetrievedMilestone.isComplete())
        {
            updateNotificationStatus();
            updateSocialConnectStatus();
        }
        // Otherwise we rely on the complete listener
    }

    private void handleTopBannerClicked()
    {
        getNavigator().pushFragment(InviteFriendFragment.class, null, Navigator.PUSH_UP_FROM_BOTTOM);
    }

    private void updateNotificationStatus()
    {
        final UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        if (currentUserProfile != null)
        {
            if (emailNotification != null)
            {
                emailNotification.setChecked(currentUserProfile.emailNotificationsEnabled);
            }

            if (pushNotification != null)
            {
                pushNotification.setChecked(currentUserProfile.pushNotificationsEnabled);
            }

            if (pushNotificationSound != null)
            {
                pushNotificationSound.setEnabled(currentUserProfile.pushNotificationsEnabled);
            }

            if (pushNotificationVibrate != null)
            {
                pushNotificationVibrate.setEnabled(currentUserProfile.pushNotificationsEnabled);
            }

            if (currentUserProfile.pushNotificationsEnabled)
            {
                pushNotificationManager.enablePush();
            }
            else
            {
                pushNotificationManager.disablePush();
            }
        }
    }

    private boolean changeEmailNotification(boolean enable)
    {
        progressDialog = getProgressDialog();
        progressDialog.setMessage(getString(R.string.settings_notifications_email_alert_message));
        progressDialog.setTitle(getString(R.string.settings_notifications_email_alert_title));

        userServiceWrapper.updateProfilePropertyEmailNotifications(currentUserBaseKeyHolder.getCurrentUserBaseKey(), enable,
                createUserProfileCallback());
        return false;
    }

    private boolean changePushNotification(boolean enable)
    {
        progressDialog = getProgressDialog();
        progressDialog.setMessage(getString(R.string.settings_notifications_push_alert_title));
        progressDialog.setTitle(getString(R.string.settings_notifications_push_alert_message));
        progressDialog.show();

        userServiceWrapper.updateProfilePropertyPushNotifications(currentUserBaseKeyHolder.getCurrentUserBaseKey(), enable,
                createUserProfileCallback());
        return false;
    }

    private boolean changeSharing(SocialNetworkEnum socialNetwork, boolean enable)
    {
        THLog.d(TAG, "Sharing is asked to change");
        currentSocialNetworkConnect = socialNetwork;
        if (enable)
        {
            switch (socialNetwork)
            {
                case FB:
                    getProgressDialog().setMessage(getString(R.string.connecting_to_facebook));
                    getProgressDialog().setTitle(getString(R.string.facebook));
                    getProgressDialog().show();
                    facebookUtils.get().logIn(getActivity(), socialConnectCallback);
                    break;
                case TW:
                    getProgressDialog().setMessage(getString(R.string.connecting_to_twitter));
                    getProgressDialog().setTitle(getString(R.string.twitter));
                    getProgressDialog().show();
                    twitterUtils.get().logIn(getActivity(), socialConnectCallback);
                    break;
                case TH:
                    break;
                case LI:
                    getProgressDialog().setMessage(getString(R.string.connecting_to_linkedin));
                    getProgressDialog().setTitle(getString(R.string.linkedin));
                    getProgressDialog().show();
                    linkedInUtils.get().logIn(getActivity(), socialConnectCallback);
                    break;
            }
        }
        else
        {
            getProgressDialog().setMessage(getString(R.string.connecting_tradehero_only));
            getProgressDialog().setTitle(getString(R.string.please_wait));
            getProgressDialog().show();
            socialService.disconnect(
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                    new SocialNetworkFormDTO(socialNetwork),
                    createSocialDisconnectCallback());

            if (socialNetwork.getAuthenticationHeader().equals(THUser.getCurrentAuthenticationType()))
            {
                effectSignOut();
            }
        }
        return false;
    }

    private Callback<UserProfileDTO> createSocialDisconnectCallback()
    {
        return new SocialLinkingCallback()
        {
            @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
            {
                super.success(userProfileDTO, thResponse);
                THUser.removeCredential(currentSocialNetworkConnect.getAuthenticationHeader());
            }
        };
    }

    private Callback<UserProfileDTO> createSocialConnectCallback()
    {
        return new SocialLinkingCallback();
    }

    private void updateSocialConnectStatus()
    {
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (facebookSharing != null)
            {
                facebookSharing.setChecked(updatedUserProfileDTO.fbLinked);
            }
            if (twitterSharing != null)
            {
                twitterSharing.setChecked(updatedUserProfileDTO.twLinked);
            }
            if (linkedInSharing != null)
            {
                linkedInSharing.setChecked(updatedUserProfileDTO.liLinked);
            }
            THLog.d(TAG, "Sharing is updated");
        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        getSherlockActivity().getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.settings));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getNavigator().popFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }

    private void handleSendLoveClicked()
    {
        THToast.show("Love");
        final String appName = "TradeHero";
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        }
    }

    private void handleSendFeedbackClicked()
    {
        startActivity(Intent.createChooser(VersionUtils.getSupportEmailIntent(getSherlockActivity()), ""));
    }

    private void handleSendFeedbackLongClicked()
    {
        startActivity(Intent.createChooser(VersionUtils.getSupportEmailIntent(getSherlockActivity(), true), ""));
    }

    private void handleFaqClicked()
    {
        String faqUrl = getResources().getString(R.string.th_faq_url);
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.BUNDLE_KEY_URL, faqUrl);
        getNavigator().pushFragment(WebViewFragment.class, bundle);
    }

    private void handleProfileClicked()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SettingsProfileFragment.BUNDLE_KEY_SHOW_BUTTON_BACK, true);
        getNavigator().pushFragment(SettingsProfileFragment.class, bundle);
    }

    private void handlePaypalClicked()
    {
        getNavigator().pushFragment(SettingsPayPalFragment.class);
    }

    private void handleTransactionHistoryClicked()
    {
        getNavigator().pushFragment(SettingsTransactionHistoryFragment.class);
    }

    private void handleRestorePurchaseClicked()
    {
        if (userInteractor.popErrorConditional() == null)
        {
            userInteractor.launchRestoreSequence();
        }
    }

    private Callback<UserProfileDTO> createUserProfileCallback()
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
            {
                userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            }

            @Override protected void failure(THException ex)
            {
                THToast.show(ex);
            }

            @Override protected void finish()
            {
                progressDialog.hide();
                updateNotificationStatus();
            }
        };
    }

    private void handleResetHelpScreensClicked()
    {
        // TODO
        THToast.show("Not implemented yet");
    }

    private void handleClearCacheClicked()
    {
        getProgressDialog().setMessage(getString(R.string.cache_clearing_alert_title));
        getProgressDialog().setTitle(getString(R.string.cache_clearing_alert_message));
        getProgressDialog().show();
        new SlowedAsyncTask<Void, Void, Void>(500)
        {
            @Override protected Void doBackgroundAction(Void... voids)
            {
                LruMemFileCache.getInstance().flush();
                return null;
            }

            @Override protected void onPostExecute(Void aVoid)
            {
                handleCacheCleared();
            }
        }.execute();
    }

    private void handleCacheCleared()
    {
        getProgressDialog().setTitle(R.string.cache_cleared_alert_title);
        getProgressDialog().setMessage("");
        getView().postDelayed(new Runnable()
        {
            @Override public void run()
            {
                progressDialog.hide();
            }
        }, 500);
    }

    private void handleSignOutClicked()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(R.string.settings_misc_sign_out_are_you_sure)
                .setCancelable(true)
                .setNegativeButton(R.string.settings_misc_sign_out_no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.settings_misc_sign_out_yes, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        effectSignOut();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void effectSignOut()
    {
        getProgressDialog().setMessage(getString(R.string.settings_misc_sign_out_alert_message));
        getProgressDialog().setTitle(getString(R.string.settings_misc_sign_out_alert_title));
        getProgressDialog().show();

        THLog.d(TAG, "Before signout current user base key " + currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        sessionService.logout(createSignOutCallback());
    }

    private Callback<UserProfileDTO> createSignOutCallback()
    {
        return new Callback<UserProfileDTO>()
        {
            @Override
            public void success(UserProfileDTO o, Response response)
            {
                THUser.clearCurrentUser();
                progressDialog.dismiss();
                ActivityHelper.launchAuthentication(getActivity());
                getActivity().finish();
                // TODO clear caches
                THLog.d(TAG, "After successful signout current user base key " + currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
            }

            @Override public void failure(RetrofitError error)
            {
                progressDialog.setTitle(R.string.settings_misc_sign_out_failed);
                progressDialog.setMessage("");
                getView().postDelayed(new Runnable()
                {
                    @Override public void run()
                    {
                        progressDialog.dismiss();
                    }
                }, 3000);
            }
        };
    }

    private void handleAboutClicked()
    {
        getNavigator().pushFragment(AboutFragment.class);
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.loading_loading),
                getString(R.string.please_wait), true);
        progressDialog.hide();
        return progressDialog;
    }

    private LogInCallback socialConnectCallback = new LogInCallback()
    {
        @Override public void done(UserBaseDTO user, THException ex)
        {
            // when user cancel the process
            progressDialog.dismiss();
        }

        @Override public void onStart()
        {
        }

        @Override public boolean onSocialAuthDone(JSONObject json)
        {
            socialService.connect(
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                    UserFormFactory.create(json),
                    createSocialConnectCallback());
            progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), currentSocialNetworkConnect.getName()));
            return false;
        }
    };

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {

        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCache.get().put(currentUserBaseKeyHolder.getCurrentUserBaseKey(), userProfileDTO);
        }

        @Override protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialog.dismiss();
            updateSocialConnectStatus();
        }
    }

    private class SettingsUserProfileRetrievedCompleteListener implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            updateNotificationStatus();
            updateSocialConnectStatus();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }
}
