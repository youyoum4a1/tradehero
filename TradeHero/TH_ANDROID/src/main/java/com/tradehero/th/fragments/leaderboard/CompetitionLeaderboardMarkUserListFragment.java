package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOUtil;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.CompetitionCacheRx;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

abstract public class CompetitionLeaderboardMarkUserListFragment extends LeaderboardMarkUserListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserListFragment.class.getName() + ".providerId";
    private static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserListFragment.class.getName() + ".competitionId";

    @Inject ProviderCacheRx providerCache;
    @Inject CompetitionCacheRx competitionCache;
    @Inject ProviderUtil providerUtil;
    @Inject CompetitionLeaderboardCacheRx competitionLeaderboardCache;
    @Inject CompetitionDTOUtil competitionDTOUtil;

    @Nullable private Subscription providerSubscription;
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    @Nullable private Subscription competitionSubscription;
    protected CompetitionId competitionId;
    protected CompetitionDTO competitionDTO;

    protected THIntentPassedListener webViewTHIntentPassedListener;
    protected WebViewFragment webViewFragment;
    protected CompetitionLeaderboardMarkUserListAdapter competitionAdapter;
    @Nullable private Subscription competitionLeaderboardSubscription;
    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull public static ProviderId getProviderId(@NonNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    public static void putCompetition(@NonNull Bundle args, @NonNull CompetitionDTO competitionDTO)
    {
        putCompetitionId(args, competitionDTO.getCompetitionId());
        putLeaderboardDefKey(args, competitionDTO.leaderboard.getLeaderboardDefKey());
    }

    public static void putCompetitionId(@NonNull Bundle args, @NonNull CompetitionId competitionId)
    {
        args.putBundle(BUNDLE_KEY_COMPETITION_ID, competitionId.getArgs());
    }

    @NonNull public static CompetitionId getCompetitionId(@NonNull Bundle args)
    {
        return new CompetitionId(args.getBundle(BUNDLE_KEY_COMPETITION_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = getProviderId(getArguments());
        competitionId = getCompetitionId(getArguments());
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

    @Override @NonNull protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(getActivity(), leaderboardDefKey.key);
    }

    protected void setupCompetitionAdapter()
    {
        if (providerDTO != null)
        {
            competitionAdapter = createCompetitionLeaderboardMarkUserAdapter();
            leaderboardMarkUserListView.setAdapter(competitionAdapter);
        }

        if (competitionAdapter != null && competitionLeaderboardDTO != null)
        {
            competitionAdapter.setCompetitionLeaderboardDTO(competitionLeaderboardDTO);
            competitionAdapter.notifyDataSetChanged();
        }
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
        fetchProvider();
        fetchCompetition();
    }

    @Override public void onStop()
    {
        unsubscribe(providerSubscription);
        providerSubscription = null;
        unsubscribe(competitionSubscription);
        competitionSubscription = null;
        unsubscribe(competitionLeaderboardSubscription);
        competitionLeaderboardSubscription = null;
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    protected void fetchProvider()
    {
        unsubscribe(providerSubscription);
        providerSubscription = AndroidObservable.bindFragment(
                this,
                providerCache.get(providerId))
                .subscribe(createProviderObserver());
    }

    protected Observer<Pair<ProviderId, ProviderDTO>> createProviderObserver()
    {
        return new ProviderObserver();
    }

    protected class ProviderObserver implements Observer<Pair<ProviderId, ProviderDTO>>
    {
        @Override public void onNext(Pair<ProviderId, ProviderDTO> pair)
        {
            providerDTO = pair.second;
            setupCompetitionAdapter();
            updateCurrentRankHeaderViewWithProvider();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_provider_info);
        }
    }

    private void updateCurrentRankHeaderViewWithProvider()
    {
        if (getRankHeaderView() != null && getRankHeaderView() instanceof CompetitionLeaderboardMarkUserStockOwnRankingView)
        {
            CompetitionLeaderboardMarkUserStockOwnRankingView rankingView = (CompetitionLeaderboardMarkUserStockOwnRankingView) getRankHeaderView();
            rankingView.setProviderDTO(providerDTO);
        }
    }

    protected void fetchCompetition()
    {
        unsubscribe(competitionSubscription);
        competitionSubscription = AndroidObservable.bindFragment(
                this,
                competitionCache.get(competitionId))
                .doOnNext(pair -> fetchCompetitionLeaderboard(pair.second))
                .subscribe(createCompetitionObserver());
    }

    protected Observer<Pair<CompetitionId, CompetitionDTO>> createCompetitionObserver()
    {
        return new CompetitionObserver();
    }

    protected class CompetitionObserver implements Observer<Pair<CompetitionId, CompetitionDTO>>
    {
        @Override public void onNext(Pair<CompetitionId, CompetitionDTO> pair)
        {
            competitionDTO = pair.second;
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_provider_competition);
        }
    }

    protected void fetchCompetitionLeaderboard(@NonNull CompetitionDTO competitionDTO)
    {
        unsubscribe(competitionLeaderboardSubscription);
        CompetitionLeaderboardId key = competitionDTOUtil.getCompetitionLeaderboardId(providerId, competitionDTO.getCompetitionId());
        competitionLeaderboardSubscription = AndroidObservable.bindFragment(
                this,
                competitionLeaderboardCache.get(key)
                        .map(pair -> pair.second))
                .subscribe(this::linkWith,
                        this::handleFetchCompetitionLeaderboardFailed);
    }

    protected void linkWith(@NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        this.competitionLeaderboardDTO = competitionLeaderboardDTO;
        setupCompetitionAdapter();
        updateCurrentRankHeaderViewWithCompetitionLeaderboard();
    }

    protected void handleFetchCompetitionLeaderboardFailed(@NonNull Throwable e)
    {
        Timber.d("ProviderPrizeAdsCallBack failure!");
    }

    @Override @LayoutRes protected int getCurrentRankLayoutResId()
    {
        return R.layout.lbmu_item_own_ranking_competition_mode;
    }

    @Override protected void setupOwnRankingView(@NonNull View userRankingHeaderView)
    {
        if (userRankingHeaderView instanceof CompetitionLeaderboardMarkUserStockItemView)
        {
            CompetitionLeaderboardMarkUserStockItemView competitionLeaderboardCurrentUserRankHeaderView =
                    (CompetitionLeaderboardMarkUserStockItemView) userRankingHeaderView;
            competitionLeaderboardCurrentUserRankHeaderView.setProviderDTO(providerDTO);
        }
        super.setupOwnRankingView(userRankingHeaderView);
    }

    protected void updateCurrentRankHeaderViewWithCompetitionLeaderboard()
    {
        if (competitionLeaderboardDTO != null
                && getRankHeaderView() != null
                && getRankHeaderView() instanceof CompetitionLeaderboardMarkUserStockOwnRankingView)
        {
            CompetitionLeaderboardMarkUserStockOwnRankingView ownRankingView = (CompetitionLeaderboardMarkUserStockOwnRankingView) getRankHeaderView();
            ownRankingView.setPrizeDTOSize(competitionLeaderboardDTO.prizes != null? competitionLeaderboardDTO.prizes.size() : 0);
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
            this.webViewFragment = navigator.get().pushFragment(WebViewFragment.class, args);
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
            return navigator.get();
        }

        @Override protected Class<?> getClassToPop()
        {
            return CompetitionLeaderboardMarkUserListFragment.class;
        }
    }

    protected class CompetitionLeaderboardMarkUserListViewFragmentListLoaderCallback extends LeaderboardMarkUserListViewFragmentListLoaderCallback
    {
        @Override public void onLoadFinished(ListLoader<StocksLeaderboardUserDTO> loader, List<StocksLeaderboardUserDTO> data)
        {
            competitionAdapter.notifyDataSetChanged();
            super.onLoadFinished(loader, data);
        }
    }
}
