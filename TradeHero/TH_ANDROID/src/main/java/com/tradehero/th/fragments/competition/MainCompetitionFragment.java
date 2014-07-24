package com.tradehero.th.fragments.competition;

import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderDisplayCellListKey;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLegalMentionsView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDisplayCellDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLegalDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePortfolioDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListClosedFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListOnGoingFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.CompetitionListCache;
import com.tradehero.th.persistence.competition.ProviderDisplayCellListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.GraphicUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Routable(
        "providers/:providerId"
)
public class MainCompetitionFragment extends CompetitionFragment
{
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.competition_zone_list) AbsListView listView;
    @InjectView(R.id.btn_trade_now) Button btnTradeNow;

    private CompetitionZoneListItemAdapter competitionZoneListItemAdapter;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private BaseWebViewFragment webViewFragment;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject CompetitionListCache competitionListCache;
    @Inject ProviderDisplayCellListCache providerDisplayListCellCache;
    @Inject ProviderUtil providerUtil;
    @Inject GraphicUtil graphicUtil;
    @Inject THIntentFactory thIntentFactory;

    protected UserProfileCompactDTO portfolioUserCompactDTO;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected CompetitionDTOList competitionDTOs;
    private DTOCacheNew.Listener<ProviderId, CompetitionDTOList> competitionListCacheFetchListener;
    private ProviderDisplayCellDTOList providerDisplayCellDTOList;
    private DTOCacheNew.Listener<ProviderDisplayCellListKey, ProviderDisplayCellDTOList> displayCellListCacheFetchListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.webViewTHIntentPassedListener = new MainCompetitionWebViewTHIntentPassedListener();
        this.competitionListCacheFetchListener = createCompetitionListCacheListener();
        this.displayCellListCacheFetchListener = createDisplayCellListCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main_competition, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
        if (this.progressBar != null)
        {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        if (this.listView != null)
        {
            this.listView.setOnItemClickListener(createAdapterViewItemClickListener());
        }
        placeAdapterInList();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onStart()
    {
        super.onStart();
        detachUserProfileCache();
        userProfileCacheListener = createProfileCacheListener();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());

        detachCompetitionListCacheTask();
        competitionListCache.register(providerId, competitionListCacheFetchListener);
        competitionListCache.getOrFetchAsync(providerId);

        detachDisplayCellListCacheTask();
        ProviderDisplayCellListKey providerDisplayCellListKey = new ProviderDisplayCellListKey(providerId);
        providerDisplayListCellCache.register(providerDisplayCellListKey, displayCellListCacheFetchListener);
        providerDisplayListCellCache.getOrFetchAsync(providerDisplayCellListKey);
    }

    @Override public void onResume()
    {
        super.onResume();
        // We came back into view so we have to forget the web fragment
        if (this.webViewFragment != null)
        {
            this.webViewFragment.setThIntentPassedListener(null);
        }
        this.webViewFragment = null;
    }

    @Override public void onStop()
    {
        detachUserProfileCache();
        detachCompetitionListCacheTask();
        detachDisplayCellListCacheTask();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (this.listView != null)
        {
            this.listView.setOnItemClickListener(null);
        }
        if (this.competitionZoneListItemAdapter != null)
        {
            this.competitionZoneListItemAdapter.setParentOnLegalElementClicked(null);
        }
        this.competitionZoneListItemAdapter = null;
        this.progressBar = null;
        this.listView = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.competitionListCacheFetchListener = null;
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    private void detachUserProfileCache()
    {
        if (userProfileCacheListener != null)
        {
            userProfileCache.unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    private void detachCompetitionListCacheTask()
    {
        competitionListCache.unregister(competitionListCacheFetchListener);
    }

    private void detachDisplayCellListCacheTask()
    {
        providerDisplayListCellCache.unregister(displayCellListCacheFetchListener);
    }

    protected void linkWith(UserProfileCompactDTO userProfileCompactDTO, boolean andDisplay)
    {
        this.portfolioUserCompactDTO = userProfileCompactDTO;
        placeAdapterInList();
    }

    @Override protected void linkWith(@NotNull ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        placeAdapterInList();
        if (andDisplay)
        {
            displayActionBarTitle();
            displayTradeNowButton();
        }
    }

    protected void linkWith(CompetitionDTOList competitionIds, boolean andDisplay)
    {
        this.competitionDTOs = competitionIds;
        placeAdapterInList();
    }

    protected void linkWith(ProviderDisplayCellDTOList providerDisplayCellDTOList, boolean andDisplay)
    {
        this.providerDisplayCellDTOList = providerDisplayCellDTOList;
        placeAdapterInList();
    }

    protected void placeAdapterInList()
    {
        if (providerDTO != null && competitionDTOs != null && providerDisplayCellDTOList != null)
        {
            if (progressBar != null)
            {
                progressBar.setVisibility(View.GONE);
            }
            CompetitionZoneListItemAdapter newAdapter = new CompetitionZoneListItemAdapter(
                    getActivity(),
                    getActivity().getLayoutInflater(),
                    R.layout.competition_zone_item,
                    R.layout.competition_zone_trade_now,
                    R.layout.competition_zone_ads,
                    R.layout.competition_zone_header,
                    R.layout.competition_zone_portfolio,
                    R.layout.competition_zone_leaderboard_item,
                    R.layout.competition_zone_legal_mentions);
            newAdapter.setParentOnLegalElementClicked(new MainCompetitionLegalClickedListener());
            newAdapter.setPortfolioUserProfileCompactDTO(portfolioUserCompactDTO);
            newAdapter.setProvider(providerDTO);
            newAdapter.setCompetitionDTOs(competitionDTOs);
            newAdapter.setDisplayCellDTOS(providerDisplayCellDTOList);

            CompetitionZoneListItemAdapter currentAdapterCopy = this.competitionZoneListItemAdapter;
            if (currentAdapterCopy != null)
            {
                currentAdapterCopy.setParentOnLegalElementClicked(null);
                currentAdapterCopy.clear();
            }

            this.competitionZoneListItemAdapter = newAdapter;

            if (this.listView != null)
            {
                this.listView.setAdapter(this.competitionZoneListItemAdapter);
            }
        }
    }

    private void displayActionBarTitle()
    {
        if (providerDTO != null
                && providerDTO.specificResources != null
                && providerDTO.specificResources.mainCompetitionFragmentTitleResId > 0)
        {
            setActionBarTitle(providerDTO.specificResources.mainCompetitionFragmentTitleResId);
        }
        else if (this.providerDTO == null || this.providerDTO.name == null)
        {
            setActionBarTitle("");
        }
        else
        {
            setActionBarTitle(this.providerDTO.name);
        }
    }

    @OnClick(R.id.btn_trade_now)
    public void handleTradeNowClicked()
    {
        pushTradeNowElement();
    }

    private void displayTradeNowButton()
    {
        if (providerDTO != null)
        {
            btnTradeNow.setVisibility(View.VISIBLE);

            int bgColor = graphicUtil.parseColor(providerDTO.hexColor);
            StateListDrawable stateListDrawable = graphicUtil.createStateListDrawable(getActivity(), bgColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                btnTradeNow.setBackground(stateListDrawable);
            }
            else
            {
                btnTradeNow.setBackgroundDrawable(stateListDrawable);
            }

            int textColor = graphicUtil.parseColor(providerDTO.textHexColor, graphicUtil.getContrastingColor(bgColor));
            btnTradeNow.setTextColor(textColor);
        }
    }

    //<editor-fold desc="Click Handling">
    private void handleItemClicked(@NotNull CompetitionZoneDTO competitionZoneDTO)
    {
        if (competitionZoneDTO instanceof CompetitionZonePortfolioDTO)
        {
            pushPortfolioElement((CompetitionZonePortfolioDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneVideoDTO)
        {
            pushVideoElement((CompetitionZoneVideoDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneWizardDTO)
        {
            pushWizardElement((CompetitionZoneWizardDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneLeaderboardDTO)
        {
            pushLeaderboardElement((CompetitionZoneLeaderboardDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneLegalDTO)
        {
            pushLegalElement((CompetitionZoneLegalDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneAdvertisementDTO)
        {
            pushAdvertisement((CompetitionZoneAdvertisementDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneDisplayCellDTO)
        {
            handleDisplayCellClicked((CompetitionZoneDisplayCellDTO) competitionZoneDTO);
        }
        // TODO others?
    }

    private void pushAdvertisement(@NotNull CompetitionZoneAdvertisementDTO competitionZoneDTO)
    {
        AdDTO adDTO = competitionZoneDTO.getAdDTO();
        if (adDTO != null && adDTO.redirectUrl != null)
        {
            Bundle args = new Bundle();
            String url = providerUtil.appendUserId(adDTO.redirectUrl, '&');
            CompetitionWebViewFragment.putUrl(args, url);
            getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
        }
    }

    private void pushTradeNowElement()
    {
        Bundle args = new Bundle();
        ProviderSecurityListFragment.putProviderId(args, providerId);
        ProviderSecurityListFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
        getDashboardNavigator().pushFragment(ProviderSecurityListFragment.class, args);
    }

    private void pushPortfolioElement(@NotNull CompetitionZonePortfolioDTO competitionZoneDTO)
    {
        // TODO We need to be able to launch async when the portfolio Id is finally not null
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null)
        {
            Bundle args = new Bundle();
            PositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
            PositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
            PositionListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
            CompetitionLeaderboardPositionListFragment.putProviderId(args, providerId);
            getDashboardNavigator().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
        }
    }

    private void pushVideoElement(@NotNull CompetitionZoneVideoDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        ProviderVideoListFragment.putProviderId(args, providerId);
        ProviderVideoListFragment.putApplicablePortfolioId(args, providerDTO.getAssociatedOwnedPortfolioId(currentUserId.toUserBaseKey()));
        getDashboardNavigator().pushFragment(ProviderVideoListFragment.class, args);
    }

    private void pushWizardElement(@NotNull CompetitionZoneWizardDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();

        String competitionUrl = competitionZoneDTO.getWebUrl();
        if (competitionUrl == null)
        {
            competitionUrl = providerUtil.getWizardPage(providerId);
            CompetitionWebViewFragment.putIsOptionMenuVisible(args, false);
        }

        CompetitionWebViewFragment.putUrl(args, competitionUrl);
        this.webViewFragment = getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    private void pushLeaderboardElement(@NotNull CompetitionZoneLeaderboardDTO competitionZoneDTO)
    {
        LeaderboardDefDTO leaderboardDefDTO = competitionZoneDTO.competitionDTO.leaderboard;
        Bundle args = new Bundle();
        args.putBundle(CompetitionLeaderboardMarkUserListFragment.BUNDLE_KEY_PROVIDER_ID,
                providerId.getArgs());
        args.putBundle(CompetitionLeaderboardMarkUserListFragment.BUNDLE_KEY_COMPETITION_ID,
                competitionZoneDTO.competitionDTO.getCompetitionId().getArgs());
        CompetitionLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
        args.putString(CompetitionLeaderboardMarkUserListFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE,
                competitionZoneDTO.competitionDTO.name);
        args.putString(CompetitionLeaderboardMarkUserListFragment.BUNDLE_KEY_LEADERBOARD_DEF_DESC,
                leaderboardDefDTO.desc);

        LeaderboardUserDTO leaderboardUserDTO = competitionZoneDTO.competitionDTO.leaderboardUser;
        if (leaderboardUserDTO != null)
        {
            CompetitionLeaderboardMarkUserListFragment.putCurrentUserRank(args,
                    leaderboardUserDTO.ordinalPosition > 0 ? leaderboardUserDTO.ordinalPosition + 1 : null);
            CompetitionLeaderboardMarkUserListFragment.putROIValueToBeShown(args, leaderboardUserDTO.roiInPeriod);
        }
        else
        {
            CompetitionLeaderboardMarkUserListFragment.putCurrentUserRank(args, CompetitionLeaderboardMarkUserListFragment.FLAG_USER_NOT_RANKED);
        }

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null)
        {
            CompetitionLeaderboardMarkUserListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        if (competitionZoneDTO.competitionDTO.leaderboard.isWithinUtcRestricted())
        {
            getDashboardNavigator().pushFragment(CompetitionLeaderboardMarkUserListOnGoingFragment.class,
                    args);
        }
        else
        {
            getDashboardNavigator().pushFragment(CompetitionLeaderboardMarkUserListClosedFragment.class,
                    args);
        }
    }

    private void pushLegalElement(@NotNull CompetitionZoneLegalDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        if ((competitionZoneDTO).requestedLink.equals(CompetitionZoneLegalDTO.LinkType.RULES))
        {
            CompetitionWebViewFragment.putUrl(args, providerUtil.getRulesPage(providerId));
        }
        else
        {
            CompetitionWebViewFragment.putUrl(args, providerUtil.getTermsPage(providerId));
        }
        getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
    }

    private void handleDisplayCellClicked(@NotNull CompetitionZoneDisplayCellDTO competitionZoneDisplayCellDTO)
    {
        String redirectUrl = competitionZoneDisplayCellDTO.getRedirectUrl();
        if (redirectUrl != null)
        {
            Uri uri = Uri.parse(redirectUrl);
            if (thIntentFactory.isHandlableScheme(uri.getScheme()))
            {
                if (uri.getHost().equalsIgnoreCase(getActivity().getString(R.string.intent_host_web)))
                {
                    String url = uri.getQueryParameter("url");
                    if (url != null)
                    {
                        Timber.d("Opening this page: %s", url);
                        Bundle bundle = new Bundle();
                        CompetitionWebViewFragment.putUrl(bundle, url);
                        this.webViewFragment = getDashboardNavigator().pushFragment(WebViewFragment.class, bundle);
                        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
                    }
                }
                else
                {
                    //TODO Confirm with tho on how this router works
                    try
                    {
                        thIntentFactory.create(getPassedIntent(redirectUrl));
                    } catch (IndexOutOfBoundsException e)
                    {
                        Timber.e(e, "Failed to create intent with string %s", redirectUrl);
                    }
                }
            }
        }
    }

    public Intent getPassedIntent(String url)
    {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
    //</editor-fold>

    private AdapterView.OnItemClickListener createAdapterViewItemClickListener()
    {
        return new MainCompetitionFragmentItemClickListener();
    }

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createProfileCacheListener()
    {
        return new MainCompetitionUserProfileCacheListener();
    }

    private class MainCompetitionFragmentItemClickListener
            implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            if (adapterView == null)
            {
                Timber.e(new NullPointerException("adapterView was null"), "onItemClient");
            }
            else
            {
                Object itemClicked = adapterView.getItemAtPosition(i);
                if (itemClicked == null)
                {
                    Timber.e(new NullPointerException("itemClicked was null"), "onItemClient");
                }
                else
                {
                    handleItemClicked((CompetitionZoneDTO) itemClicked);
                }
            }
        }
    }

    private class MainCompetitionWebViewTHIntentPassedListener
            extends CompetitionWebFragmentTHIntentPassedListener
    {
        public MainCompetitionWebViewTHIntentPassedListener()
        {
            super();
        }

        @Override protected BaseWebViewFragment getApplicableWebViewFragment()
        {
            return webViewFragment;
        }

        @Override protected OwnedPortfolioId getApplicablePortfolioId()
        {
            return MainCompetitionFragment.this.getApplicablePortfolioId();
        }

        @Override protected ProviderId getProviderId()
        {
            return providerId;
        }

        @Override protected Navigator getNavigator()
        {
            return MainCompetitionFragment.this.getDashboardNavigator();
        }

        @Override protected Class<?> getClassToPop()
        {
            return MainCompetitionFragment.class;
        }
    }

    private class MainCompetitionLegalClickedListener
            implements CompetitionZoneLegalMentionsView.OnElementClickedListener
    {
        @Override public void onElementClicked(CompetitionZoneDTO competitionZoneLegalDTO)
        {
            handleItemClicked(competitionZoneLegalDTO);
        }
    }

    private class MainCompetitionUserProfileCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey providerId, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_your_user_profile));
            Timber.e("Error fetching the profile info %s", key, error);
        }
    }

    private DTOCacheNew.Listener<ProviderId, CompetitionDTOList> createCompetitionListCacheListener()
    {
        return new MainCompetitionCompetitionListCacheListener();
    }

    private class MainCompetitionCompetitionListCacheListener
            implements DTOCacheNew.Listener<ProviderId, CompetitionDTOList>
    {
        @Override public void onDTOReceived(@NotNull ProviderId providerId, @NotNull CompetitionDTOList value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull ProviderId key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_competition_list));
            Timber.e("Error fetching the list of competition info %s", key, error);
        }
    }

    private DTOCacheNew.Listener<ProviderDisplayCellListKey, ProviderDisplayCellDTOList> createDisplayCellListCacheListener()
    {
        return new MainCompetitionDisplayCellListCacheListener();
    }

    private class MainCompetitionDisplayCellListCacheListener
            implements DTOCacheNew.Listener<ProviderDisplayCellListKey, ProviderDisplayCellDTOList>
    {
        @Override public void onDTOReceived(@NotNull ProviderDisplayCellListKey providerId, @NotNull ProviderDisplayCellDTOList value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull ProviderDisplayCellListKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_competition_display_cell_list));
            Timber.e("Error fetching the list of competition info %s", key, error);
        }
    }
}
