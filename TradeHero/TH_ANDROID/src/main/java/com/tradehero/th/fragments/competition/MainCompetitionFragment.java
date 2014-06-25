package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.thoj.route.Routable;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionIdList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.CompetitionId;
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
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLegalDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePortfolioDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneTradeNowDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListClosedFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListOnGoingFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.competition.CompetitionListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Routable(
        "providers/:providerId"
)
public class MainCompetitionFragment extends CompetitionFragment
{
    private ActionBar actionBar;
    private ProgressBar progressBar;
    AbsListView listView;
    private CompetitionZoneListItemAdapter competitionZoneListItemAdapter;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private BaseWebViewFragment webViewFragment;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject CompetitionListCache competitionListCache;
    @Inject CompetitionCache competitionCache;
    @Inject ProviderUtil providerUtil;

    protected UserProfileCompactDTO portfolioUserCompactDTO;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected List<CompetitionId> competitionIds;
    private DTOCacheNew.Listener<ProviderId, CompetitionIdList> competitionListCacheFetchListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.webViewTHIntentPassedListener = new MainCompetitionWebViewTHIntentPassedListener();
        this.competitionListCacheFetchListener = createCompetitionListCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main_competition, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        this.progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        if (this.progressBar != null)
        {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        this.listView = (AbsListView) view.findViewById(R.id.competition_zone_list);
        if (this.listView != null)
        {
            this.listView.setOnItemClickListener(createAdapterViewItemClickListener());
        }
        placeAdapterInList();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);
        displayActionBarTitle();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
        this.actionBar = null;
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

    protected void linkWith(UserProfileCompactDTO userProfileCompactDTO, boolean andDisplay)
    {
        this.portfolioUserCompactDTO = userProfileCompactDTO;
        placeAdapterInList();
    }

    @Override protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        placeAdapterInList();
        if (progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }
        if (andDisplay)
        {
            displayActionBarTitle();
        }
    }

    protected void linkWith(List<CompetitionId> competitionIds, boolean andDisplay)
    {
        this.competitionIds = competitionIds;
        placeAdapterInList();
    }

    protected void placeAdapterInList()
    {
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
        newAdapter.setCompetitionDTOs(competitionCache.get(competitionIds));

        CompetitionZoneListItemAdapter currentAdapterCopy = this.competitionZoneListItemAdapter;
        if (currentAdapterCopy != null)
        {
            currentAdapterCopy.setParentOnLegalElementClicked(null);
        }

        this.competitionZoneListItemAdapter = newAdapter;

        if (this.listView != null)
        {
            this.listView.setAdapter(this.competitionZoneListItemAdapter);
        }
    }

    private void displayActionBarTitle()
    {
        if (this.actionBar != null)
        {
            if (providerSpecificResourcesDTO != null
                    && providerSpecificResourcesDTO.mainCompetitionFragmentTitleResId > 0)
            {
                this.actionBar.setTitle(
                        providerSpecificResourcesDTO.mainCompetitionFragmentTitleResId);
            }
            else if (this.providerDTO == null || this.providerDTO.name == null)
            {
                this.actionBar.setTitle("");
            }
            else
            {
                this.actionBar.setTitle(this.providerDTO.name);
            }
        }
    }

    //<editor-fold desc="Click Handling">
    private void handleItemClicked(@NotNull CompetitionZoneDTO competitionZoneDTO)
    {
        if (competitionZoneDTO instanceof CompetitionZoneTradeNowDTO)
        {
            pushTradeNowElement((CompetitionZoneTradeNowDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZonePortfolioDTO)
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
        // TODO others?
    }

    private void pushAdvertisement(@NotNull CompetitionZoneAdvertisementDTO competitionZoneDTO)
    {
        AdDTO adDTO = competitionZoneDTO.getAdDTO();
        if (adDTO != null && adDTO.redirectUrl != null)
        {
            Bundle args = new Bundle();
            String url = providerUtil.appendUserId(adDTO.redirectUrl, '&', currentUserId.toUserBaseKey());
            args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL, url);
            getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
        }
    }

    private void pushTradeNowElement(@NotNull CompetitionZoneTradeNowDTO competitionZoneDTO)
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
            getDashboardNavigator().pushFragment(PositionListFragment.class, args);
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
            args.putBoolean(CompetitionWebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, false);
        }
        
        args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL, competitionUrl);
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
        CompetitionLeaderboardMarkUserListFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
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
            args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL,
                    providerUtil.getRulesPage(providerId));
        }
        else
        {
            args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL,
                    providerUtil.getTermsPage(providerId));
        }
        getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
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
        @Override public void onDTOReceived(UserBaseKey providerId, UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_your_user_profile));
            Timber.e("Error fetching the profile info %s", key, error);
        }
    }

    private DTOCacheNew.Listener<ProviderId, CompetitionIdList> createCompetitionListCacheListener()
    {
        return new MainCompetitionCompetitionListCacheListener();
    }

    private class MainCompetitionCompetitionListCacheListener
            implements DTOCacheNew.Listener<ProviderId, CompetitionIdList>
    {
        @Override public void onDTOReceived(ProviderId providerId, CompetitionIdList value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(ProviderId key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_competition_leaderboard_list));
            Timber.e("Error fetching the list of competition info %s", key, error);
        }
    }
}
