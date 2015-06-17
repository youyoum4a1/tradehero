package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class CompetitionLeaderboardMarkUserRecyclerFragment extends LeaderboardMarkUserRecyclerFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserRecyclerFragment.class.getName() + ".providerId";
    private static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserRecyclerFragment.class.getName() + ".competitionId";

    @Inject ProviderCacheRx providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject CompetitionLeaderboardCacheRx competitionLeaderboardCache;

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected CompetitionId competitionId;

    protected THIntentPassedListener webViewTHIntentPassedListener;
    protected WebViewFragment webViewFragment;
    protected CompetitionLeaderboardWrapperRecyclerAdapter competitionAdapter;
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
        providerId = getProviderId(getArguments());
        competitionId = getCompetitionId(getArguments());
        super.onCreate(savedInstanceState);
        this.webViewTHIntentPassedListener = new CompetitionLeaderboardListWebViewTHIntentPassedListener();
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
        fetchAll();
    }

    @Override public void onDestroy()
    {
        this.competitionAdapter = null;
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    @Override protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, null, perPage);
    }

    @NonNull @Override protected LeaderboardMarkUserRecyclerAdapter<LeaderboardItemDisplayDTO> createItemViewAdapter()
    {
        return new CompetitionLeaderboardMarkUserRecyclerAdapter(getActivity(), R.layout.lbmu_item_roi_competition_mode,
                R.layout.lbmu_item_own_ranking_competition_mode,
                new LeaderboardKey(leaderboardDefKey.key));
    }

    @Override protected RecyclerView.Adapter onImplementAdapter(RecyclerView.Adapter adapter)
    {
        competitionAdapter = new CompetitionLeaderboardWrapperRecyclerAdapter(getActivity(), adapter);
        return competitionAdapter;
    }

    protected void fetchAll()
    {
        CompetitionLeaderboardId competitionLeaderboardId = new CompetitionLeaderboardId(providerId.key, competitionId.key);
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                Observable.combineLatest(
                        providerCache.get(providerId)
                                .map(new PairGetSecond<ProviderId, ProviderDTO>()),
                        competitionLeaderboardCache.get(competitionLeaderboardId)
                                .map(new PairGetSecond<CompetitionLeaderboardId, CompetitionLeaderboardDTO>()),
                        new Func2<ProviderDTO, CompetitionLeaderboardDTO, CompetitionLeaderboardDTO>()
                        {
                            @Override public CompetitionLeaderboardDTO call(ProviderDTO providerDTO,
                                    CompetitionLeaderboardDTO competitionLeaderboardDTO)
                            {
                                linkWith(providerDTO);
                                linkWith(competitionLeaderboardDTO);
                                return competitionLeaderboardDTO;
                            }
                        }))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CompetitionLeaderboardDTO>()
                {
                    @Override public void call(CompetitionLeaderboardDTO competitionLeaderboardDTO)
                    {
                        addExtraTiles();
                    }
                }, new ToastOnErrorAction()));
    }

    protected void linkWith(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
    }

    protected void linkWith(@NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        this.competitionLeaderboardDTO = competitionLeaderboardDTO;
    }

    @Override protected void onNext(@NonNull PagedLeaderboardKey key, @NonNull LeaderboardMarkedUserItemDisplayDto.DTOList value)
    {
        super.onNext(key, value);
        addExtraTiles();
    }

    private void addExtraTiles()
    {
        if (competitionLeaderboardDTO != null && competitionLeaderboardDTO.ads != null)
        {
            int realSize = itemViewAdapter.getItemCount();
            for (int i = competitionLeaderboardDTO.adStartRow; i < realSize; i += competitionLeaderboardDTO.adFrequencyRows)
            {
                int randomAds = (int) (Math.random() * competitionLeaderboardDTO.ads.size());
                competitionAdapter.addExtraItem(i, new CompetitionAdsExtraItem(competitionLeaderboardDTO.ads.get(randomAds)));
                realSize++; //Add +1 because technically, the size of the list has grown by 1 when we add an extra tile.
            }
        }
    }

    @NonNull @Override public DTOCacheRx<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>> getCache()
    {
        return new CompetitionLeaderboardMarkUserItemViewDTOCacheRx(
                getResources(),
                currentUserId,
                providerId,
                userProfileCache,
                providerCache,
                competitionLeaderboardCache);
    }

    @NonNull @Override protected Observable<LeaderboardMarkedUserItemDisplayDto.Requisite> fetchOwnRankingInfoObservables()
    {
        return Observable.zip(
                super.fetchOwnRankingInfoObservables(),
                providerCache.getOne(providerId),
                competitionLeaderboardCache.getOne(new CompetitionLeaderboardId(providerId.key, competitionId.key)),
                new Func3<LeaderboardMarkedUserItemDisplayDto.Requisite,
                        Pair<ProviderId, ProviderDTO>,
                        Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO>, LeaderboardMarkedUserItemDisplayDto.Requisite>()
                {
                    @Override public LeaderboardMarkedUserItemDisplayDto.Requisite call(
                            LeaderboardMarkedUserItemDisplayDto.Requisite requisite,
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

    @Override protected LeaderboardMarkedUserItemDisplayDto createNotRankedOwnRankingDTO(LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        if (requisite instanceof CompetitionLeaderboardMarkUserItemView.Requisite)
        {
            CompetitionLeaderboardMarkUserItemView.Requisite thisRequisite = (CompetitionLeaderboardMarkUserItemView.Requisite) requisite;
            if (requisite.currentLeaderboardUserDTO == null)
            {
                //TODO clicked event and rules
                return new CompetitionLeaderboardOwnRankingDisplayDTO(getResources(), currentUserId,
                        requisite.currentUserProfileDTO, thisRequisite.providerDTO);
            }
        }
        return super.createNotRankedOwnRankingDTO(requisite);
    }

    @Override protected LeaderboardMarkedUserItemDisplayDto createRankedOwnRankingDTO(LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        if (requisite instanceof CompetitionLeaderboardMarkUserItemView.Requisite)
        {
            CompetitionLeaderboardMarkUserItemView.Requisite thisRequisite = (CompetitionLeaderboardMarkUserItemView.Requisite) requisite;
            if (requisite.currentLeaderboardUserDTO != null)
            {
                CompetitionLeaderboardDTO competitionLeaderboardDTO = thisRequisite.competitionLeaderboardDTO;
                CompetitionLeaderboardItemDisplayDTO dto = new CompetitionLeaderboardOwnRankingDisplayDTO(
                        getResources(),
                        currentUserId,
                        requisite.currentLeaderboardUserDTO,
                        requisite.currentUserProfileDTO,
                        thisRequisite.providerDTO,
                        competitionLeaderboardDTO);
                dto.setIsMyOwnRanking(true);
                return dto;
            }
        }
        return super.createRankedOwnRankingDTO(requisite);
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
            return CompetitionLeaderboardMarkUserRecyclerFragment.this.webViewFragment;
        }

        @Override protected OwnedPortfolioId getApplicablePortfolioId()
        {
            return CompetitionLeaderboardMarkUserRecyclerFragment.this.getApplicablePortfolioId();
        }

        @Override protected ProviderId getProviderId()
        {
            return CompetitionLeaderboardMarkUserRecyclerFragment.this.providerId;
        }

        @Override protected DashboardNavigator getNavigator()
        {
            return navigator.get();
        }

        @Override protected Class<?> getClassToPop()
        {
            return CompetitionLeaderboardMarkUserRecyclerFragment.class;
        }
    }
}
