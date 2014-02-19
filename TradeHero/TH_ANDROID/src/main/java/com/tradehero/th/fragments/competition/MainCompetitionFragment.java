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
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionIdList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLegalMentionsView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLegalDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePortfolioDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneTradeNowDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListViewFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.competition.CompetitionListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by xavier on 1/17/14.
 */
public class MainCompetitionFragment extends CompetitionFragment
{
    public static final String TAG = MainCompetitionFragment.class.getSimpleName();

    private ActionBar actionBar;
    private ProgressBar progressBar;
    private AbsListView listView;
    private CompetitionZoneListItemAdapter competitionZoneListItemAdapter;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private WebViewFragment webViewFragment;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject CompetitionListCache competitionListCache;
    @Inject CompetitionCache competitionCache;
    @Inject ProviderUtil providerUtil;

    protected UserProfileCompactDTO portfolioUserCompactDTO;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> profileCacheListener;

    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> profileCacheFetchTask;
    protected List<CompetitionId> competitionIds;
    private DTOCache.Listener<ProviderId, CompetitionIdList> competitionListCacheListener;
    private DTOCache.GetOrFetchTask<ProviderId, CompetitionIdList> competitionListCacheFetchTask;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.webViewTHIntentPassedListener = new MainCompetitionWebViewTHIntentPassedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main_competition, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {

        this.progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
        if (this.progressBar != null)
        {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        this.listView = (AbsListView) view.findViewById(R.id.competition_zone_list);
        if (this.listView != null)
        {
            this.listView.setOnItemClickListener(new MainCompetitionFragmentItemClickListener());
        }
        placeAdapterInList();

        this.profileCacheListener = new MainCompetitionUserProfileCacheListener();
        this.competitionListCacheListener = new MainCompetitionCompetitionListCacheListener();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
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
        detachUserProfileCacheTask();
        profileCacheFetchTask = userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), profileCacheListener);
        profileCacheFetchTask.execute();

        detachCompetitionListCacheTask();
        competitionListCacheFetchTask = competitionListCache.getOrFetch(providerId, competitionListCacheListener);
        competitionListCacheFetchTask.execute();
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
        detachUserProfileCacheTask();
        detachCompetitionListCacheTask();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (this.competitionZoneListItemAdapter != null)
        {
            this.competitionZoneListItemAdapter.setParentOnLegalElementClicked(null);
        }
        this.competitionZoneListItemAdapter = null;

        this.progressBar = null;

        if (this.listView != null)
        {
            this.listView.setOnItemClickListener(null);
        }
        this.listView = null;

        this.profileCacheListener = null;
        this.competitionListCacheListener = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    private void detachUserProfileCacheTask()
    {
        if (profileCacheFetchTask != null)
        {
            profileCacheFetchTask.setListener(null);
        }
        profileCacheFetchTask = null;
    }

    private void detachCompetitionListCacheTask()
    {
        if (competitionListCacheFetchTask != null)
        {
            competitionListCacheFetchTask.setListener(null);
        }
        competitionListCacheFetchTask = null;
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

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected void placeAdapterInList()
    {
        CompetitionZoneListItemAdapter newAdapter = new CompetitionZoneListItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.competition_zone_item,
                R.layout.competition_zone_trade_now,
                R.layout.competition_zone_header,
                R.layout.competition_zone_portfolio,
                R.layout.competition_zone_leaderboard_item, R.layout.competition_zone_legal_mentions);
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
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.mainCompetitionFragmentTitleResId > 0)
            {
                this.actionBar.setTitle(providerSpecificResourcesDTO.mainCompetitionFragmentTitleResId);
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
    private void handleItemClicked(CompetitionZoneDTO competitionZoneDTO)
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

        // TODO others?
    }

    private void pushTradeNowElement(CompetitionZoneTradeNowDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
        args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, userInteractor.getApplicablePortfolioId().getArgs());
        getNavigator().pushFragment(ProviderSecurityListFragment.class, args);
    }

    private void pushPortfolioElement(CompetitionZonePortfolioDTO competitionZoneDTO)
    {
        // TODO We need to be able to launch async when the portfolio Id is finally not null
        OwnedPortfolioId ownedPortfolioId = userInteractor.getApplicablePortfolioId();
        if (ownedPortfolioId != null)
        {
            Bundle args = new Bundle();
            args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
            getNavigator().pushFragment(PositionListFragment.class, args);
        }
    }

    private void pushVideoElement(CompetitionZoneVideoDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        args.putBundle(ProviderVideoListFragment.BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
        args.putBundle(ProviderVideoListFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                providerDTO.associatedPortfolio.getPortfolioId().getArgs());
        getNavigator().pushFragment(ProviderVideoListFragment.class, args);
    }

    private void pushWizardElement(CompetitionZoneWizardDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        args.putString(WebViewFragment.BUNDLE_KEY_URL, providerUtil.getWizardPage(providerId) + "&previous=whatever");
        args.putBoolean(WebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, false);
        this.webViewFragment = (WebViewFragment) getNavigator().pushFragment(
                WebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    private void pushLeaderboardElement(CompetitionZoneLeaderboardDTO competitionZoneDTO)
    {
        LeaderboardDefDTO leaderboardDefDTO = competitionZoneDTO.competitionDTO.leaderboard;
        Bundle args = new Bundle();
        if (competitionZoneDTO.competitionDTO.leaderboard.isWithinUtcRestricted())
        {
            args.putBundle(CompetitionLeaderboardMarkUserListViewFragment.BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
            args.putBundle(CompetitionLeaderboardMarkUserListViewFragment.BUNDLE_KEY_COMPETITION_ID, competitionZoneDTO.competitionDTO.getCompetitionId().getArgs());
            args.putInt(CompetitionLeaderboardMarkUserListViewFragment.BUNDLE_KEY_LEADERBOARD_ID, leaderboardDefDTO.id);
            args.putString(CompetitionLeaderboardMarkUserListViewFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, leaderboardDefDTO.name);
            args.putString(CompetitionLeaderboardMarkUserListViewFragment.BUNDLE_KEY_LEADERBOARD_DEF_DESC, leaderboardDefDTO.desc);
            args.putBundle(CompetitionLeaderboardMarkUserListViewFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
            getNavigator().pushFragment(
                    CompetitionLeaderboardMarkUserListViewFragment.class, args);
        }
        else
        {
            args.putInt(LeaderboardMarkUserListViewFragment.BUNDLE_KEY_LEADERBOARD_ID, leaderboardDefDTO.id);
            args.putString(LeaderboardMarkUserListViewFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, leaderboardDefDTO.name);
            args.putString(LeaderboardMarkUserListViewFragment.BUNDLE_KEY_LEADERBOARD_DEF_DESC, leaderboardDefDTO.desc);
            getNavigator().pushFragment(LeaderboardMarkUserListViewFragment.class, args);
        }
    }

    private void pushLegalElement(CompetitionZoneLegalDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        if ((competitionZoneDTO).requestedLink.equals(CompetitionZoneLegalDTO.LinkType.RULES))
        {
            args.putString(WebViewFragment.BUNDLE_KEY_URL, providerUtil.getRulesPage(providerId));
        }
        else
        {
            args.putString(WebViewFragment.BUNDLE_KEY_URL, providerUtil.getTermsPage(providerId));
        }
        getNavigator().pushFragment(WebViewFragment.class, args);
    }
    //</editor-fold>

    private class MainCompetitionFragmentItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            THLog.d(TAG, "onItemClient");
            handleItemClicked((CompetitionZoneDTO) adapterView.getItemAtPosition(i));
        }
    }

    private class MainCompetitionWebViewTHIntentPassedListener extends CompetitionWebFragmentTHIntentPassedListener
    {
        public MainCompetitionWebViewTHIntentPassedListener()
        {
            super();
        }

        @Override protected WebViewFragment getApplicableWebViewFragment()
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
            return MainCompetitionFragment.this.getNavigator();
        }

        @Override protected Class<?> getClassToPop()
        {
            return MainCompetitionFragment.class;
        }
    }

    private class MainCompetitionLegalClickedListener implements CompetitionZoneLegalMentionsView.OnElementClickedListener
    {
        @Override public void onElementClicked(CompetitionZoneDTO competitionZoneLegalDTO)
        {
            handleItemClicked(competitionZoneLegalDTO);
        }
    }

    private class MainCompetitionUserProfileCacheListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey providerId, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_your_user_profile));
            THLog.e(TAG, "Error fetching the profile info " + key, error);
        }
    }

    private class MainCompetitionCompetitionListCacheListener implements DTOCache.Listener<ProviderId, CompetitionIdList>
    {
        @Override public void onDTOReceived(ProviderId providerId, CompetitionIdList value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(ProviderId key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_competition_leaderboard_list));
            THLog.e(TAG, "Error fetching the list of competition info " + key, error);
        }
    }
}
