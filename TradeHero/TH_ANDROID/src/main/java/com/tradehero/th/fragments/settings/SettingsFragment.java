package com.tradehero.th.fragments.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorerAlertUtil;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Routable("settings")
public final class SettingsFragment extends DashboardPreferenceFragment
{
    private static final String KEY_SOCIAL_NETWORK_TO_CONNECT = SettingsFragment.class.getName() + ".socialNetworkToConnectKey";

    @Inject THBillingInteractor billingInteractor;
    @Inject protected Provider<THUIBillingRequest> billingRequestProvider;
    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    private Integer restoreRequestCode;

    // TODO something belong to Google Play should not be here, generic util class for all store is expected
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject @ServerEndpoint StringPreference serverEndpoint;

    @Inject Analytics analytics;

    private SocialNetworkEnum socialNetworkToConnectTo;
    @Inject protected SocialConnectSettingViewHolderContainer socialConnectSettingViewHolderContainer;
    @Inject protected SendLoveViewHolder sendLoveViewHolder;
    @Inject protected SendFeedbackViewHolder sendFeedbackViewHolder;
    @Inject protected FaqViewHolder faqViewHolder;
    @Inject protected ProfilePreferenceViewHolder profilePreferenceViewHolder;
    @Inject protected LocationCountrySettingsViewHolder locationCountrySettingsViewHolder;
    @Inject protected PayPalSettingViewHolder payPalSettingViewHolder;
    @Inject protected AlipaySettingViewHolder alipaySettingViewHolder;
    @Inject protected TransactionHistoryViewHolder transactionHistoryViewHolder;
    @Inject protected ReferralCodeViewHolder referralCodeViewHolder;
    @Inject protected SignOutSettingViewHolder signOutSettingViewHolder;
    @Inject protected UserTranslationSettingsViewHolder userTranslationSettingsViewHolder;
    @Inject protected EmailNotificationSettingViewHolder emailNotificationSettingViewHolder;
    @Inject protected PushNotificationSettingViewHolder pushNotificationSettingViewHolder;
    @Inject protected ResetHelpScreensViewHolder resetHelpScreensViewHolder;
    @Inject protected ClearCacheViewHolder clearCacheViewHolder;
    @Inject protected AboutPrefViewHolder aboutPrefViewHolder;

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

    @Override public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup,
            Bundle paramBundle)
    {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        view.setBackgroundColor(getResources().getColor(R.color.white));

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
        socialConnectSettingViewHolderContainer.destroyViews();
        sendFeedbackViewHolder.destroyViews();
        sendLoveViewHolder.destroyViews();
        faqViewHolder.destroyViews();
        profilePreferenceViewHolder.destroyViews();
        locationCountrySettingsViewHolder.destroyViews();
        payPalSettingViewHolder.destroyViews();
        alipaySettingViewHolder.destroyViews();
        transactionHistoryViewHolder.destroyViews();
        referralCodeViewHolder.destroyViews();
        signOutSettingViewHolder.destroyViews();
        userTranslationSettingsViewHolder.destroyViews();
        emailNotificationSettingViewHolder.destroyViews();
        pushNotificationSettingViewHolder.destroyViews();
        resetHelpScreensViewHolder.destroyViews();
        clearCacheViewHolder.destroyViews();
        aboutPrefViewHolder.destroyViews();

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        aboutPrefViewHolder = null;
        clearCacheViewHolder = null;
        resetHelpScreensViewHolder = null;
        pushNotificationSettingViewHolder = null;
        emailNotificationSettingViewHolder = null;
        userTranslationSettingsViewHolder = null;
        signOutSettingViewHolder = null;
        referralCodeViewHolder = null;
        transactionHistoryViewHolder = null;
        alipaySettingViewHolder = null;
        payPalSettingViewHolder = null;
        locationCountrySettingsViewHolder = null;
        profilePreferenceViewHolder = null;
        sendFeedbackViewHolder = null;
        sendLoveViewHolder = null;
        faqViewHolder = null;
        socialConnectSettingViewHolderContainer = null;

        this.purchaseRestorerFinishedListener = null;
        super.onDestroy();
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
        alipaySettingViewHolder.initViews(this);
        transactionHistoryViewHolder.initViews(this);
        referralCodeViewHolder.initViews(this);
        signOutSettingViewHolder.initViews(this);
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
        socialConnectSettingViewHolderContainer.initViews(this);

        // Translations
        userTranslationSettingsViewHolder.initViews(this);

        // notification
        emailNotificationSettingViewHolder.initViews(this);
        pushNotificationSettingViewHolder.initViews(this);

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

    protected void changeSharing(SocialNetworkEnum socialNetworkEnum, boolean enable)
    {
        socialConnectSettingViewHolderContainer.changeSharing(socialNetworkEnum, enable);
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
}
