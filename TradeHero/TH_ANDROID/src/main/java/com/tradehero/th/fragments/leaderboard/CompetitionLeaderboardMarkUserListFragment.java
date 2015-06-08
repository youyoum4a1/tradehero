package com.tradehero.th.fragments.leaderboard;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.CompetitionCacheRx;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import timber.log.Timber;

public class CompetitionLeaderboardMarkUserListFragment extends LeaderboardMarkUserListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserListFragment.class.getName() + ".providerId";
    private static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserListFragment.class.getName() + ".competitionId";

    @Inject ProviderCacheRx providerCache;
    @Inject CompetitionCacheRx competitionCache;
    @Inject ProviderUtil providerUtil;
    @Inject CompetitionLeaderboardCacheRx competitionLeaderboardCache;

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected CompetitionId competitionId;
    protected CompetitionDTO competitionDTO;

    protected THIntentPassedListener webViewTHIntentPassedListener;
    protected WebViewFragment webViewFragment;
    protected CompetitionLeaderboardMarkUserListAdapter competitionAdapter;
    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;

    @NonNull private DataSetObserver innerAdapterObserver;

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
        providerId = getProviderId(getArguments());
        competitionId = getCompetitionId(getArguments());
        super.onCreate(savedInstanceState);
        this.webViewTHIntentPassedListener = new CompetitionLeaderboardListWebViewTHIntentPassedListener();
        innerAdapterObserver = new DataSetObserver()
        {
            @Override public void onChanged()
            {
                if (competitionAdapter != null)
                {
                    competitionAdapter.notifyDataSetChanged();
                }
            }

            @Override public void onInvalidated()
            {
                if (competitionAdapter != null)
                {
                    competitionAdapter.notifyDataSetInvalidated();
                }
            }
        };
        itemViewAdapter.registerDataSetObserver(innerAdapterObserver);
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

        MenuItem wizardButton = menu.findItem(R.id.btn_wizard);
        if (wizardButton != null)
        {
            wizardButton.setVisible(providerDTO != null && providerDTO.hasWizard());
        }
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
        itemViewAdapter.unregisterDataSetObserver(innerAdapterObserver);
        this.competitionAdapter = null;
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    @Override protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, null, null);
    }

    protected void setupCompetitionAdapter()
    {
        if (providerDTO != null && competitionAdapter == null)
        {
            competitionAdapter = createCompetitionLeaderboardMarkUserAdapter();
            listView.setAdapter(competitionAdapter);
        }

        if (competitionAdapter != null && competitionLeaderboardDTO != null)
        {
            competitionAdapter.setCompetitionLeaderboardDTO(competitionLeaderboardDTO);
            competitionAdapter.notifyDataSetChanged();
        }
    }

    @NonNull protected CompetitionLeaderboardMarkUserListAdapter createCompetitionLeaderboardMarkUserAdapter()
    {
        return new CompetitionLeaderboardMarkUserListAdapter(getActivity(), providerDTO, itemViewAdapter);
    }

    protected void fetchProvider()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                providerCache.get(providerId)
                        .map(new PairGetSecond<ProviderId, ProviderDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ProviderDTO>()
                        {
                            @Override public void call(ProviderDTO providerDTO)
                            {
                                linkWith(providerDTO);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_provider_info))));
    }

    protected void linkWith(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        setupCompetitionAdapter();
    }

    protected void fetchCompetition()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                competitionCache.get(competitionId)
                        .map(new PairGetSecond<CompetitionId, CompetitionDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<CompetitionDTO>()
                        {
                            @Override public void call(CompetitionDTO competition)
                            {
                                linkWith(competition);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_provider_competition),
                                "Error fetching competition info")));
    }

    protected void linkWith(@NonNull CompetitionDTO competitionDTO)
    {
        this.competitionDTO = competitionDTO;

        CompetitionLeaderboardId key = new CompetitionLeaderboardId(providerId.key, competitionId.key);
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                competitionLeaderboardCache.get(key)
                        .map(new PairGetSecond<CompetitionLeaderboardId, CompetitionLeaderboardDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<CompetitionLeaderboardDTO>()
                        {
                            @Override public void call(CompetitionLeaderboardDTO leaderboardDTO)
                            {
                                linkWith(leaderboardDTO);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                CompetitionLeaderboardMarkUserListFragment.this.handleFetchCompetitionLeaderboardFailed(error);
                            }
                        }));
    }

    protected void linkWith(@NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        this.competitionLeaderboardDTO = competitionLeaderboardDTO;
        setupCompetitionAdapter();
    }

    protected void handleFetchCompetitionLeaderboardFailed(@NonNull Throwable e)
    {
        Timber.d("ProviderPrizeAdsCallBack failure!");
    }

    @Override @LayoutRes protected int getCurrentRankLayoutResId()
    {
        return R.layout.lbmu_item_own_ranking_competition_mode;
    }

    @NonNull @Override public DTOCacheRx<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList> getCache()
    {
        return new CompetitionLeaderboardMarkUserItemViewDTOCacheRx(
                getResources(),
                currentUserId,
                providerId,
                userProfileCache,
                providerCache,
                competitionLeaderboardCache);
    }

    @NonNull @Override protected Observable<LeaderboardMarkUserItemView.Requisite> fetchOwnRankingInfoObservables()
    {
        return Observable.zip(
                super.fetchOwnRankingInfoObservables(),
                providerCache.getOne(providerId),
                competitionLeaderboardCache.getOne(new CompetitionLeaderboardId(providerId.key, competitionId.key)),
                new Func3<LeaderboardMarkUserItemView.Requisite,
                        Pair<ProviderId, ProviderDTO>,
                        Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO>, LeaderboardMarkUserItemView.Requisite>()
                {
                    @Override public LeaderboardMarkUserItemView.Requisite call(
                            LeaderboardMarkUserItemView.Requisite requisite,
                            Pair<ProviderId, ProviderDTO> providerPair,
                            Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO> competitionLeaderboardPair)
                    {
                        return new CompetitionLeaderboardMarkUserItemView.Requisite(
                                requisite,
                                providerPair,
                                competitionLeaderboardPair);
                    }
                });
    }

    @Override protected void updateCurrentRankView(@Nullable LeaderboardMarkUserItemView.Requisite requisite)
    {
        super.updateCurrentRankView(requisite);

        if (requisite instanceof CompetitionLeaderboardMarkUserItemView.Requisite)
        {
            CompetitionLeaderboardMarkUserItemView.Requisite thisRequisite = (CompetitionLeaderboardMarkUserItemView.Requisite) requisite;
            if (requisite.currentLeaderboardUserDTO == null)
            {
                ((LeaderboardMarkUserListAdapter) itemViewAdapter).isNotRanked(requisite.currentUserProfileDTO);
            }
            else
            {
                CompetitionLeaderboardDTO competitionLeaderboardDTO = thisRequisite.competitionLeaderboardDTO;
                ((LeaderboardMarkUserListAdapter) itemViewAdapter).isRanked(new CompetitionLeaderboardMarkUserOwnRankingView.DTO(
                        getResources(),
                        currentUserId,
                        requisite.currentLeaderboardUserDTO,
                        requisite.currentUserProfileDTO,
                        competitionLeaderboardDTO.prizes == null ? 0 : competitionLeaderboardDTO.prizes.size(),
                        thisRequisite.providerDTO));
            }
        }
        else
        {
            super.updateCurrentRankView(requisite);
        }
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
}
