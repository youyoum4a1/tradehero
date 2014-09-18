package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOUtil;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class CompetitionLeaderboardMarkUserListFragment extends LeaderboardMarkUserListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserListFragment.class.getName() + ".providerId";
    public static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserListFragment.class.getName() + ".competitionId";

    @Inject ProviderCache providerCache;
    @Inject CompetitionCache competitionCache;
    @Inject ProviderUtil providerUtil;
    @Inject CompetitionLeaderboardCache competitionLeaderboardCache;
    @Inject CompetitionDTOUtil competitionDTOUtil;

    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> competitionLeaderboardCacheListener;

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected CompetitionDTO competitionDTO;
    protected THIntentPassedListener webViewTHIntentPassedListener;
    protected WebViewFragment webViewFragment;
    protected CompetitionLeaderboardMarkUserListAdapter competitionAdapter;
    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;

    public static void putProviderId(@NotNull Bundle args, @NotNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NotNull public static ProviderId getProviderId(@NotNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    public static void putCompetition(@NotNull Bundle args, @NotNull CompetitionDTO competitionDTO)
    {
        putCompetitionId(args, competitionDTO.getCompetitionId());
        putLeaderboardDefKey(args, competitionDTO.leaderboard.getLeaderboardDefKey());
    }

    public static void putCompetitionId(@NotNull Bundle args, @NotNull CompetitionId competitionId)
    {
        args.putBundle(BUNDLE_KEY_COMPETITION_ID, competitionId.getArgs());
    }

    @NotNull public static CompetitionId getCompetitionId(@NotNull Bundle args)
    {
        return new CompetitionId(args.getBundle(BUNDLE_KEY_COMPETITION_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = getProviderId(getArguments());
        providerDTO = providerCache.get(providerId);
        Timber.d("providerDTO %s", providerDTO);

        CompetitionId competitionId = getCompetitionId(getArguments());
        competitionDTO = competitionCache.get(competitionId);
        Timber.d("competitionDTO %s", competitionDTO);
        competitionLeaderboardCacheListener = createCompetitionLeaderboardListener();
        this.webViewTHIntentPassedListener = new CompetitionLeaderboardListWebViewTHIntentPassedListener();
    }

    @Override protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        return new PerPagedLeaderboardKey(leaderboardDefKey.key, null, null);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        MenuItem filterMenu = menu.findItem(R.id.button_leaderboard_filter);
        if (filterMenu != null)
        {
            filterMenu.setVisible(false);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.competition_leaderboard_list_menu, menu);

        MenuItem wizardButton = menu.findItem(R.id.btn_wizard);
        if (wizardButton != null)
        {
            wizardButton.setVisible(providerDTO != null && providerDTO.hasWizard());
        }
    }

    @Override protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardDefKey.key, R.layout.lbmu_item_competition_mode);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setupCompetitionAdapter();
    }

    protected void setupCompetitionAdapter()
    {
        competitionAdapter = createCompetitionLeaderboardMarkUserAdapter();
        leaderboardMarkUserListView.setAdapter(competitionAdapter);
    }

    protected CompetitionLeaderboardMarkUserListAdapter createCompetitionLeaderboardMarkUserAdapter()
    {
        leaderboardMarkUserListAdapter.setDTOLoaderCallback(new CompetitionLeaderboardMarkUserListViewFragmentListLoaderCallback());
        return new CompetitionLeaderboardMarkUserListAdapter(getActivity(), providerDTO, leaderboardMarkUserListAdapter);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_wizard:
                pushWizardElement();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        fetchCompetitionLeaderboard();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        competitionLeaderboardCacheListener = null;
        super.onDestroy();
    }

    protected void detachCompetitionLeaderboardCache()
    {
        competitionLeaderboardCache.unregister(competitionLeaderboardCacheListener);
    }

    protected void fetchCompetitionLeaderboard()
    {
        detachCompetitionLeaderboardCache();
        CompetitionLeaderboardId key = competitionDTOUtil.getCompetitionLeaderboardId(providerId, competitionDTO.getCompetitionId());
        competitionLeaderboardCache.register(key, competitionLeaderboardCacheListener);
        competitionLeaderboardCache.getOrFetchAsync(key);
    }

    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> createCompetitionLeaderboardListener()
    {
        return new CompetitionLeaderboardCacheListener();
    }

    protected class CompetitionLeaderboardCacheListener implements DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull CompetitionLeaderboardId key, @NotNull final CompetitionLeaderboardDTO value)
        {
            competitionLeaderboardDTO = value;
            competitionAdapter.setCompetitionLeaderboardDTO(value);
            competitionAdapter.notifyDataSetChanged();
            updateCurrentRankHeaderView();
        }

        @Override public void onErrorThrown(@NotNull CompetitionLeaderboardId key, @NotNull Throwable error)
        {
            Timber.d("ProviderPrizeAdsCallBack failure!");
        }
    }

    @Override protected int getCurrentRankLayoutResId()
    {
        return R.layout.lbmu_item_own_ranking_competition_mode;
    }

    @Override protected void setupOwnRankingView(View userRankingHeaderView)
    {
        if (userRankingHeaderView instanceof CompetitionLeaderboardMarkUserItemView)
        {
            CompetitionLeaderboardMarkUserItemView competitionLeaderboardCurrentUserRankHeaderView =
                    (CompetitionLeaderboardMarkUserItemView) userRankingHeaderView;
            competitionLeaderboardCurrentUserRankHeaderView.setProviderDTO(providerDTO);
        }
        super.setupOwnRankingView(userRankingHeaderView);
    }

    @Override protected void updateCurrentRankHeaderView()
    {
        super.updateCurrentRankHeaderView();
        if (competitionLeaderboardDTO != null
                && getRankHeaderView() != null
                && getRankHeaderView() instanceof CompetitionLeaderboardMarkUserItemView)
        {
            CompetitionLeaderboardMarkUserItemView ownRankingView = (CompetitionLeaderboardMarkUserItemView) getRankHeaderView();
            if (ownRankingView.leaderboardItem != null)
            {
                ownRankingView.setPrizeDTO(competitionLeaderboardDTO.getPrizeAt(ownRankingView.leaderboardItem.ordinalPosition));
            }
        }
    }

    @Override protected void saveCurrentFilterKey()
    {
        // Do nothing
    }

    private void pushWizardElement()
    {
        Bundle args = new Bundle();
        WebViewFragment.putUrl(args, providerUtil.getWizardPage(providerId) + "&previous=whatever");
        WebViewFragment.putIsOptionMenuVisible(args, false);
        if (navigator != null)
        {
            this.webViewFragment = navigator.pushFragment(WebViewFragment.class, args);
            this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
        }
    }

    @Override protected void displayFilterIcon(MenuItem filterIcon)
    {
        if (filterIcon != null)
        {
            filterIcon.setVisible(false);
        }
    }

    @Override protected void pushFilterFragmentIn()
    {
        // Do nothing
    }

    protected class CompetitionLeaderboardListWebViewTHIntentPassedListener extends CompetitionWebFragmentTHIntentPassedListener
    {
        public CompetitionLeaderboardListWebViewTHIntentPassedListener()
        {
            super();
        }

        @Override protected WebViewFragment getApplicableWebViewFragment()
        {
            return CompetitionLeaderboardMarkUserListFragment.this.webViewFragment;
        }

        @Override protected OwnedPortfolioId getApplicablePortfolioId()
        {
            return CompetitionLeaderboardMarkUserListFragment.this.getApplicablePortfolioId();
        }

        @Override protected ProviderId getProviderId()
        {
            return CompetitionLeaderboardMarkUserListFragment.this.providerId;
        }

        @Override protected DashboardNavigator getNavigator()
        {
            return navigator;
        }

        @Override protected Class<?> getClassToPop()
        {
            return CompetitionLeaderboardMarkUserListFragment.class;
        }
    }

    protected class CompetitionLeaderboardMarkUserListViewFragmentListLoaderCallback extends LeaderboardMarkUserListViewFragmentListLoaderCallback
    {
        @Override public void onLoadFinished(ListLoader<LeaderboardUserDTO> loader, List<LeaderboardUserDTO> data)
        {
            competitionAdapter.notifyDataSetChanged();
            super.onLoadFinished(loader, data);
        }
    }
}
