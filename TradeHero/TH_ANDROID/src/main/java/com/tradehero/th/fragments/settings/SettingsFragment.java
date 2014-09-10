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
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import dagger.Lazy;

@Routable("settings")
public final class SettingsFragment extends DashboardPreferenceFragment
{
    private static final String KEY_SOCIAL_NETWORK_TO_CONNECT = SettingsFragment.class.getName() + ".socialNetworkToConnectKey";

    @Inject THBillingInteractor billingInteractor;
    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    private Integer restoreRequestCode;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject SocialServiceWrapper socialServiceWrapper;
    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    private MiddleCallback<UserProfileDTO> middleCallbackDisconnect;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject PushNotificationManager pushNotificationManager;
    @Inject @ServerEndpoint StringPreference serverEndpoint;
    @Inject Analytics analytics;
    @Inject protected TopBannerSettingViewHolder topBannerSettingViewHolder;
    @Inject protected SocialConnectSettingViewHolderContainer socialConnectSettingViewHolderContainer;
    @Inject protected SendLoveViewHolder sendLoveViewHolder;
    @Inject protected SendFeedbackViewHolder sendFeedbackViewHolder;
    @Inject protected FaqViewHolder faqViewHolder;
    @Inject protected ProfilePreferenceViewHolder profilePreferenceViewHolder;
    @Inject protected LocationCountrySettingsViewHolder locationCountrySettingsViewHolder;
    @Inject protected PayPalSettingViewHolder payPalSettingViewHolder;
    @Inject protected AlipaySettingViewHolder alipaySettingViewHolder;
    @Inject protected TransactionHistoryViewHolder transactionHistoryViewHolder;
    @Inject protected RestorePurchaseSettingViewHolder restorePurchaseSettingViewHolder;
    @Inject protected ReferralCodeSettingViewHolder referralCodeSettingViewHolder;
    @Inject protected SignOutSettingViewHolder signOutSettingViewHolder;
    @Inject protected UserTranslationSettingsViewHolder userTranslationSettingsViewHolder;
    @Inject protected EmailNotificationSettingViewHolder emailNotificationSettingViewHolder;
    @Inject protected PushNotificationSettingViewHolder pushNotificationSettingViewHolder;
    @Inject protected ResetHelpScreensViewHolder resetHelpScreensViewHolder;
    @Inject protected ClearCacheViewHolder clearCacheViewHolder;
    @Inject protected AboutPrefViewHolder aboutPrefViewHolder;

    private SocialNetworkEnum socialNetworkToConnectTo;

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
            socialConnectSettingViewHolderContainer.changeSharing(socialNetworkToConnectTo, true);
            socialNetworkToConnectTo = null;
        }
    }

    @Override public void onDestroyView()
    {
        aboutPrefViewHolder.destroyViews();
        clearCacheViewHolder.destroyViews();
        resetHelpScreensViewHolder.destroyViews();
        pushNotificationSettingViewHolder.destroyViews();
        emailNotificationSettingViewHolder.destroyViews();
        userTranslationSettingsViewHolder.destroyViews();
        signOutSettingViewHolder.destroyViews();
        referralCodeSettingViewHolder.destroyViews();
        restorePurchaseSettingViewHolder.destroyViews();
        transactionHistoryViewHolder.destroyViews();
        alipaySettingViewHolder.destroyViews();
        payPalSettingViewHolder.destroyViews();
        locationCountrySettingsViewHolder.destroyViews();
        profilePreferenceViewHolder.destroyViews();
        faqViewHolder.destroyViews();
        sendFeedbackViewHolder.destroyViews();
        sendLoveViewHolder.destroyViews();
        socialConnectSettingViewHolderContainer.destroyViews();
        topBannerSettingViewHolder.destroyViews();

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
        referralCodeSettingViewHolder = null;
        restorePurchaseSettingViewHolder = null;
        transactionHistoryViewHolder = null;
        alipaySettingViewHolder = null;
        payPalSettingViewHolder = null;
        locationCountrySettingsViewHolder = null;
        profilePreferenceViewHolder = null;
        faqViewHolder = null;
        sendFeedbackViewHolder = null;
        sendLoveViewHolder = null;
        socialConnectSettingViewHolderContainer = null;
        topBannerSettingViewHolder = null;

        super.onDestroy();
    }

    private void initPreferenceClickHandlers()
    {
        topBannerSettingViewHolder.initViews(this);

        // Sharing
        socialConnectSettingViewHolderContainer.initViews(this);

        // General
        sendLoveViewHolder.initViews(this);
        sendFeedbackViewHolder.initViews(this);
        faqViewHolder.initViews(this);

        // Account
        profilePreferenceViewHolder.initViews(this);
        locationCountrySettingsViewHolder.initViews(this);
        payPalSettingViewHolder.initViews(this);
        alipaySettingViewHolder.initViews(this);
        transactionHistoryViewHolder.initViews(this);
        restorePurchaseSettingViewHolder.initViews(this);
        referralCodeSettingViewHolder.initViews(this);
        signOutSettingViewHolder.initViews(this);

        // Translations
        userTranslationSettingsViewHolder.initViews(this);

        // Notification
        emailNotificationSettingViewHolder.initViews(this);
        pushNotificationSettingViewHolder.initViews(this);

        // Misc
        resetHelpScreensViewHolder.initViews(this);
        clearCacheViewHolder.initViews(this);
        aboutPrefViewHolder.initViews(this);

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
}
