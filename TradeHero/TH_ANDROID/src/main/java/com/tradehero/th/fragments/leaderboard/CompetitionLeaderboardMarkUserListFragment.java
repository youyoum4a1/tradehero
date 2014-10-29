package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import com.tradehero.th.persistence.competition.CompetitionCacheRx;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
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

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected CompetitionId competitionId;
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

    @Override @NotNull protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(
                getActivity(), leaderboardDefKey.key, R.layout.lbmu_item_competition_mode);
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

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    protected void fetchProvider()
    {
        AndroidObservable.bindFragment(
                this,
                providerCache.get(providerId))
                .observeOn(AndroidSchedulers.mainThread())
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
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_provider_info);
        }
    }

    protected void fetchCompetition()
    {
        AndroidObservable.bindFragment(
                this,
                competitionCache.get(competitionId))
                .observeOn(AndroidSchedulers.mainThread())
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

    protected void fetchCompetitionLeaderboard(@NotNull CompetitionDTO competitionDTO)
    {
        CompetitionLeaderboardId key = competitionDTOUtil.getCompetitionLeaderboardId(providerId, competitionDTO.getCompetitionId());
        AndroidObservable.bindFragment(
                this,
                competitionLeaderboardCache.get(key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createCompetitionLeaderboardObserver());
    }

    protected Observer<Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO>> createCompetitionLeaderboardObserver()
    {
        return new CompetitionLeaderboardObserver();
    }

    protected class CompetitionLeaderboardObserver implements Observer<Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO>>
    {
        @Override public void onNext(Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO> pair)
        {
            competitionLeaderboardDTO = pair.second;
            setupCompetitionAdapter();
            updateCurrentRankHeaderView();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.d("ProviderPrizeAdsCallBack failure!");
        }
    }

    @Override @LayoutRes protected int getCurrentRankLayoutResId()
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
        @Override public void onLoadFinished(ListLoader<LeaderboardUserDTO> loader, List<LeaderboardUserDTO> data)
        {
            competitionAdapter.notifyDataSetChanged();
            super.onLoadFinished(loader, data);
        }
    }
}
