package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorerAlertUtil;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.QQUtils;
import com.tradehero.th.utils.TwitterUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import timber.log.Timber;

@Routable("settings")
public final class SettingsFragment extends DashboardPreferenceFragment
{
    private static final String KEY_SOCIAL_NETWORK_TO_CONNECT = SettingsFragment.class.getName() + ".socialNetworkToConnectKey";

    @Inject THBillingInteractor billingInteractor;
    @Inject protected Provider<THUIBillingRequest> billingRequestProvider;
    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    private Integer restoreRequestCode;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject SocialServiceWrapper socialServiceWrapper;
    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    private MiddleCallback<UserProfileDTO> middleCallbackDisconnect;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject PushNotificationManager pushNotificationManager;
    // TODO something belong to Google Play should not be here, generic util class for all store is expected
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject @ServerEndpoint StringPreference serverEndpoint;

    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<TwitterUtils> twitterUtils;
    @Inject Lazy<LinkedInUtils> linkedInUtils;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject Lazy<QQUtils> qqUtils;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject MainCredentialsPreference mainCredentialsPreference;

    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;

    private SocialNetworkEnum socialNetworkToConnectTo;
    private ProgressDialog progressDialog;
    private CheckBoxPreference facebookSharing;
    private SocialNetworkEnum currentSocialNetworkConnect;
    private CheckBoxPreference twitterSharing;
    private CheckBoxPreference linkedInSharing;
    private CheckBoxPreference weiboSharing;
    private CheckBoxPreference qqSharing;
    @Inject protected SendLoveViewHolder sendLoveViewHolder;
    @Inject protected SendFeedbackViewHolder sendFeedbackViewHolder;
    @Inject protected FaqViewHolder faqViewHolder;
    @Inject protected ProfilePreferenceViewHolder profilePreferenceViewHolder;
    @Inject protected LocationCountrySettingsViewHolder locationCountrySettingsViewHolder;
    @Inject protected PayPalSettingViewHolder payPalSettingViewHolder;
    @Inject protected AlipayViewHolder alipayViewHolder;
    @Inject protected TransactionHistoryViewHolder transactionHistoryViewHolder;
    @Inject protected ReferralCodeViewHolder referralCodeViewHolder;
    @Inject protected SignOutViewHolder signOutViewHolder;
    @Inject protected UserTranslationSettingsViewHolder userTranslationSettingsViewHolder;
    @Inject protected ResetHelpScreensViewHolder resetHelpScreensViewHolder;
    @Inject protected ClearCacheViewHolder clearCacheViewHolder;
    @Inject protected AboutPrefViewHolder aboutPrefViewHolder;
    private CheckBoxPreference pushNotification;
    private CheckBoxPreference emailNotification;
    private CheckBoxPreference pushNotificationSound;
    private CheckBoxPreference pushNotificationVibrate;
    private UserProfileRetrievedMilestone currentUserProfileRetrievedMilestone;
    private SettingsUserProfileRetrievedCompleteListener
            currentUserProfileRetrievedMilestoneListener;
    private LogInCallback socialConnectLogInCallback;

    public static void putSocialNetworkToConnect(@NotNull Bundle args, @NotNull SocialNetworkEnum socialNetwork)
    {
        args.putString(KEY_SOCIAL_NETWORK_TO_CONNECT, socialNetwork.name());
    }

    public static void putSocialNetworkToConnect(@NotNull Bundle args, @Nullable SocialShareFormDTO shareFormDTO)
    {
        if (shareFormDTO instanceof TimelineItemShareFormDTO &&
                ((TimelineItemShareFormDTO) shareFormDTO).timelineItemShareRequestDTO != null &&
                ((TimelineItemShareFormDTO) shareFormDTO).timelineItemShareRequestDTO.socialNetwork != null)
        {
            putSocialNetworkToConnect(args, ((TimelineItemShareFormDTO) shareFormDTO).timelineItemShareRequestDTO.socialNetwork);
        }
    }

    @Nullable public static SocialNetworkEnum getSocialNetworkToConnect(@Nullable Bundle args)
    {
        if (args == null)
        {
            return null;
        }
        String name = args.getString(KEY_SOCIAL_NETWORK_TO_CONNECT);
        if (name == null)
        {
            return null;
        }
        return SocialNetworkEnum.valueOf(name);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.settings);

        DaggerUtils.inject(this);

        createSocialConnectLogInCallback();

        purchaseRestorerFinishedListener = new BillingPurchaseRestorer.OnPurchaseRestorerListener()
        {
            @Override public void onPurchaseRestored(
                    int requestCode,
                    List restoredPurchases,
                    List failedRestorePurchases,
                    List failExceptions)
            {
                if (Integer.valueOf(requestCode).equals(restoreRequestCode))
                {
                    restoreRequestCode = null;
                }
            }
        };

        this.socialNetworkToConnectTo = getSocialNetworkToConnect(getArguments());
    }

    private void createSocialConnectLogInCallback()
    {
        socialConnectLogInCallback = new LogInCallback()
        {
            @Override public void done(UserLoginDTO user, THException ex)
            {
                // when user cancel the process
                if (!isDetached())
                {
                    progressDialog.hide();
                }
            }

            @Override public void onStart()
            {
            }

            @Override public boolean onSocialAuthDone(JSONCredentials json)
            {
                detachMiddleCallbackConnect();
                middleCallbackConnect = socialServiceWrapper.connect(
                        currentUserId.toUserBaseKey(),
                        UserFormFactory.create(json),
                        createSocialConnectCallback());
                if (!isDetached())
                {
                    progressDialog.setMessage(
                            String.format(getString(R.string.authentication_connecting_tradehero),
                                    currentSocialNetworkConnect.getName()));
                }
                return false;
            }
        };
    }

    @Override public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup,
            Bundle paramBundle)
    {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        view.setBackgroundColor(getResources().getColor(R.color.white));

        detachCurrentUserProfileMilestone();
        this.currentUserProfileRetrievedMilestone =
                new UserProfileRetrievedMilestone(currentUserId.toUserBaseKey());
        currentUserProfileRetrievedMilestoneListener =
                new SettingsUserProfileRetrievedCompleteListener();
        this.currentUserProfileRetrievedMilestone.setOnCompleteListener(
                currentUserProfileRetrievedMilestoneListener);

        if (userProfileCache.get().get(currentUserId.toUserBaseKey()) == null)
        {
            progressDialog =
                    progressDialogUtil.show(getActivity(), R.string.loading_required_information,
                            R.string.alert_dialog_please_wait);
        }
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
        initInfo();
        super.onViewCreated(view, savedInstanceState);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.settings));

    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Settings));
        if (socialNetworkToConnectTo != null)
        {
            changeSharing(socialNetworkToConnectTo, true);
            socialNetworkToConnectTo = null;
        }
    }

    @Override public void onDestroyView()
    {
        sendFeedbackViewHolder.destroyViews();
        sendLoveViewHolder.destroyViews();
        faqViewHolder.destroyViews();
        profilePreferenceViewHolder.destroyViews();
        locationCountrySettingsViewHolder.destroyViews();
        payPalSettingViewHolder.destroyViews();
        alipayViewHolder.destroyViews();
        transactionHistoryViewHolder.destroyViews();
        referralCodeViewHolder.destroyViews();
        signOutViewHolder.destroyViews();
        userTranslationSettingsViewHolder.destroyViews();
        resetHelpScreensViewHolder.destroyViews();
        clearCacheViewHolder.destroyViews();
        aboutPrefViewHolder.destroyViews();

        detachMiddleCallbackUpdateUserProfile();
        detachCurrentUserProfileMilestone();
        detachMiddleCallbackConnect();
        detachMiddleCallbackDisconnect();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        aboutPrefViewHolder = null;
        clearCacheViewHolder = null;
        resetHelpScreensViewHolder = null;
        userTranslationSettingsViewHolder = null;
        signOutViewHolder = null;
        referralCodeViewHolder = null;
        transactionHistoryViewHolder = null;
        alipayViewHolder = null;
        payPalSettingViewHolder = null;
        locationCountrySettingsViewHolder = null;
        profilePreferenceViewHolder = null;
        sendFeedbackViewHolder = null;
        sendLoveViewHolder = null;
        faqViewHolder = null;

        socialConnectLogInCallback = null;
        this.currentUserProfileRetrievedMilestoneListener = null;
        this.purchaseRestorerFinishedListener = null;
        super.onDestroy();
    }

    private void detachMiddleCallbackUpdateUserProfile()
    {
        if (middleCallbackUpdateUserProfile != null)
        {
            middleCallbackUpdateUserProfile.setPrimaryCallback(null);
        }
        middleCallbackUpdateUserProfile = null;
    }

    protected void detachCurrentUserProfileMilestone()
    {
        if (this.currentUserProfileRetrievedMilestone != null)
        {
            this.currentUserProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        this.currentUserProfileRetrievedMilestone = null;
    }

    protected void detachMiddleCallbackConnect()
    {
        if (middleCallbackConnect != null)
        {
            middleCallbackConnect.setPrimaryCallback(null);
        }
        middleCallbackConnect = null;
    }

    protected void detachMiddleCallbackDisconnect()
    {
        if (middleCallbackDisconnect != null)
        {
            middleCallbackDisconnect.setPrimaryCallback(null);
        }
        middleCallbackDisconnect = null;
    }

    private BillingPurchaseRestorer.OnPurchaseRestorerListener createPurchaseRestorerListener()
    {
        return new BillingPurchaseRestorer.OnPurchaseRestorerListener()
        {
            @Override public void onPurchaseRestored(int requestCode, List restoredPurchases,
                    List failedRestorePurchases, List failExceptions)
            {
                Timber.d("onPurchaseRestoreFinished3");
                IABPurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                        getActivity(),
                        restoredPurchases,
                        failedRestorePurchases,
                        IABPurchaseRestorerAlertUtil.createFailedRestoreClickListener(getActivity(),
                                new Exception())); // TODO have a better exception
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

        sendLoveViewHolder.initViews(this);
        sendFeedbackViewHolder.initViews(this);
        faqViewHolder.initViews(this);
        profilePreferenceViewHolder.initViews(this);

        // Account
        payPalSettingViewHolder.initViews(this);
        alipayViewHolder.initViews(this);
        transactionHistoryViewHolder.initViews(this);
        referralCodeViewHolder.initViews(this);
        signOutViewHolder.initViews(this);
        locationCountrySettingsViewHolder.initViews(this);

        Preference restorePurchaseBlock =
                findPreference(getString(R.string.key_settings_primary_restore_purchases));
        if (restorePurchaseBlock != null)
        {
            restorePurchaseBlock.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener()
                    {
                        @Override public boolean onPreferenceClick(Preference preference)
                        {
                            handleRestorePurchaseClicked();
                            return true;
                        }
                    });
        }

        // Sharing
        facebookSharing = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_sharing_facebook));
        if (facebookSharing != null)
        {
            facebookSharing.setOnPreferenceChangeListener(createPreferenceChangeListenerSharing(SocialNetworkEnum.FB));
        }
        twitterSharing = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_sharing_twitter));
        if (twitterSharing != null)
        {
            twitterSharing.setOnPreferenceChangeListener(createPreferenceChangeListenerSharing(SocialNetworkEnum.TW));
        }
        linkedInSharing = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_sharing_linked_in));
        if (linkedInSharing != null)
        {
            linkedInSharing.setOnPreferenceChangeListener(createPreferenceChangeListenerSharing(SocialNetworkEnum.LN));
        }
        weiboSharing = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_sharing_weibo));
        if (weiboSharing != null)
        {
            weiboSharing.setOnPreferenceChangeListener(createPreferenceChangeListenerSharing(SocialNetworkEnum.WB));
        }
        qqSharing = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_sharing_qq));
        if (qqSharing != null)
        {
            qqSharing.setOnPreferenceChangeListener(createPreferenceChangeListenerSharing(SocialNetworkEnum.QQ));
        }

        // Translations
        userTranslationSettingsViewHolder.initViews(this);

        // notification
        pushNotification = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_notifications_push));
        if (pushNotification != null)
        {
            pushNotification.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue)
                        {
                            return changePushNotification((boolean) newValue);
                        }
                    });
        }

        emailNotification = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_notifications_email));
        if (emailNotification != null)
        {
            emailNotification.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue)
                        {
                            return changeEmailNotification((boolean) newValue);
                        }
                    });
        }

        pushNotificationSound = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_notifications_push_alert_sound));
        if (pushNotificationSound != null)
        {
            pushNotificationSound.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue)
                        {
                            pushNotificationManager.setSoundEnabled((boolean) newValue);
                            return true;
                        }
                    });
        }

        pushNotificationVibrate = (CheckBoxPreference) findPreference(
                getString(R.string.key_settings_notifications_push_alert_vibrate));
        if (pushNotificationVibrate != null)
        {
            pushNotificationVibrate.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue)
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

        resetHelpScreensViewHolder.initViews(this);
        clearCacheViewHolder.initViews(this);
        aboutPrefViewHolder.initViews(this);
    }

    private void initInfo()
    {
        Preference version = findPreference(getString(R.string.key_settings_misc_version_server));
        String serverPath = serverEndpoint.get().replace("http://", "").replace("https://", "");
        PackageInfo packageInfo = null;
        String timeStr;
        try
        {
            packageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        if (packageInfo != null)
        {
            timeStr = (String) DateFormat.format(
                    getActivity().getString(R.string.data_format_d_mmm_yyyy_kk_mm),
                    packageInfo.lastUpdateTime);
            timeStr = timeStr + "(" + packageInfo.lastUpdateTime + ")";
            version.setSummary(timeStr);
        }
        version.setTitle(VersionUtils.getVersionId(getActivity()) + " - " + serverPath);
    }

    private void handleTopBannerClicked()
    {
        getNavigator().pushFragment(FriendsInvitationFragment.class, null,
                Navigator.PUSH_UP_FROM_BOTTOM, null);
    }

    private void updateNotificationStatus()
    {
        final UserProfileDTO currentUserProfile =
                userProfileCache.get().get(currentUserId.toUserBaseKey());
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
        progressDialog = progressDialogUtil.show(getActivity(),
                R.string.settings_notifications_email_alert_title,
                R.string.settings_notifications_email_alert_message);

        detachCurrentUserProfileMilestone();
        middleCallbackUpdateUserProfile =
                userServiceWrapper.updateProfilePropertyEmailNotifications(
                        currentUserId.toUserBaseKey(), enable,
                        createUserProfileCallback());
        return false;
    }

    private boolean changePushNotification(boolean enable)
    {
        progressDialog = progressDialogUtil.show(getActivity(),
                R.string.settings_notifications_push_alert_title,
                R.string.settings_notifications_push_alert_message);

        detachCurrentUserProfileMilestone();
        middleCallbackUpdateUserProfile = userServiceWrapper.updateProfilePropertyPushNotifications(
                currentUserId.toUserBaseKey(), enable,
                createUserProfileCallback());
        return false;
    }

    private Preference.OnPreferenceChangeListener createPreferenceChangeListenerSharing(
            final SocialNetworkEnum socialNetwork)
    {
        return new Preference.OnPreferenceChangeListener()
        {
            @Override public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                return changeSharing(socialNetwork, (boolean) newValue);
            }
        };
    }

    private boolean changeSharing(SocialNetworkEnum socialNetwork, boolean enable)
    {
        Timber.d("Sharing is asked to change");
        currentSocialNetworkConnect = socialNetwork;
        if (enable)
        {
            switch (socialNetwork)
            {
                case FB:
                    progressDialog = progressDialogUtil.show(getActivity(),
                            R.string.facebook,
                            R.string.authentication_connecting_to_facebook);

                    facebookUtils.get().logIn(getActivity(), socialConnectLogInCallback);
                    break;
                case TW:
                    progressDialog = progressDialogUtil.show(getActivity(),
                            R.string.twitter,
                            R.string.authentication_twitter_connecting);
                    twitterUtils.get().logIn(getActivity(), socialConnectLogInCallback);
                    break;
                case TH:
                    break;
                case LN:
                    progressDialog = progressDialogUtil.show(getActivity(),
                            R.string.linkedin,
                            R.string.authentication_connecting_to_linkedin);
                    linkedInUtils.get().logIn(getActivity(), socialConnectLogInCallback);
                    break;
                case WB:
                    progressDialog = progressDialogUtil.show(getActivity(),
                            R.string.sina_weibo,
                            R.string.authentication_connecting_to_weibo);
                    weiboUtils.get().logIn(getActivity(), socialConnectLogInCallback);
                    break;
                case QQ:
                    progressDialog = progressDialogUtil.show(getActivity(),
                            R.string.tencent_qq,
                            R.string.authentication_connecting_to_qq);
                    qqUtils.get().logIn(getActivity(), socialConnectLogInCallback);
                    break;
            }
        }
        else
        {
            progressDialog = progressDialogUtil.show(getActivity(),
                    R.string.alert_dialog_please_wait,
                    R.string.authentication_connecting_tradehero_only);
            detachMiddleCallbackDisconnect();
            middleCallbackDisconnect = socialServiceWrapper.disconnect(
                    currentUserId.toUserBaseKey(),
                    new SocialNetworkFormDTO(socialNetwork),
                    createSocialDisconnectCallback());

            CredentialsDTO mainCredentials = mainCredentialsPreference.getCredentials();
            if (mainCredentials != null && socialNetwork.getAuthenticationHeader().equals(mainCredentials.getAuthType()))
            {
                // TODO remove this dependency
                signOutViewHolder.effectSignOut();
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
        UserProfileDTO updatedUserProfileDTO =
                userProfileCache.get().get(currentUserId.toUserBaseKey());
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
            if (weiboSharing != null)
            {
                weiboSharing.setChecked(updatedUserProfileDTO.wbLinked);
            }
            if (qqSharing != null)
            {
                qqSharing.setChecked(updatedUserProfileDTO.qqLinked);
            }
            Timber.d("Sharing is updated");
        }
    }

    private void handleRestorePurchaseClicked()
    {
        if (restoreRequestCode != null)
        {
            billingInteractor.forgetRequestCode(restoreRequestCode);
        }
        restoreRequestCode = billingInteractor.run(createRestoreRequest());
    }

    protected THUIBillingRequest createRestoreRequest()
    {
        THUIBillingRequest request = billingRequestProvider.get();
        request.restorePurchase = true;
        request.startWithProgressDialog = true;
        request.popRestorePurchaseOutcome = true;
        request.popRestorePurchaseOutcomeVerbose = true;
        request.purchaseRestorerListener = purchaseRestorerFinishedListener;
        return request;
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

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
        }

        @Override protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialog.hide();
            updateSocialConnectStatus();
        }
    }

    private class SettingsUserProfileRetrievedCompleteListener
            implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            onFinish();
            updateNotificationStatus();
            updateSocialConnectStatus();
        }

        private void onFinish()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            onFinish();
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }
}
