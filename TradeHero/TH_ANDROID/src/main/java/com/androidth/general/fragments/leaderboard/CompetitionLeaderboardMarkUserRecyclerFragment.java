package com.androidth.general.fragments.leaderboard;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.androidth.general.api.competition.AdDTO;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.R;
import com.androidth.general.api.competition.CompetitionDTO;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.CompetitionId;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardId;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import com.androidth.general.api.leaderboard.key.PagedLeaderboardKey;
import com.androidth.general.api.leaderboard.key.PerPagedLeaderboardKey;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.androidth.general.fragments.web.WebViewIntentFragment;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.persistence.competition.CompetitionCacheRx;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import java.util.Date;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class CompetitionLeaderboardMarkUserRecyclerFragment extends LeaderboardMarkUserRecyclerFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserRecyclerFragment.class.getName() + ".providerId";
    private static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserRecyclerFragment.class.getName() + ".competitionId";
    private static final String TITLE_KEY_ID = CompetitionLeaderboardMarkUserRecyclerFragment.class.getName() + ".titleId";

    @Inject ProviderCacheRx providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject CompetitionLeaderboardCacheRx competitionLeaderboardCache;
    @Inject CompetitionCacheRx competitionCacheRx;

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected CompetitionId competitionId;

    protected THIntentPassedListener webViewTHIntentPassedListener;
    protected WebViewIntentFragment webViewFragment;
    protected CompetitionLeaderboardWrapperRecyclerAdapter competitionAdapter;
    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;
    protected CompetitionDTO competitionDTO;
    private CompetitionTimeExtraItem competitionTimeExtraItem;
    private CountDownTimer countDownTimer;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }
    public static void putTitle(@NonNull Bundle args, @NonNull String title)
    {
        args.putString(TITLE_KEY_ID,title);
    }
    @NonNull public static String getTitleKeyId(@NonNull Bundle args)
    {
        return args.getString(TITLE_KEY_ID);
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
        getSupportActionBar().setTitle(getTitleKeyId(getArguments()));
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
        this.competitionDTO = null;
        this.providerDTO = null;
        this.competitionLeaderboardDTO = null;
        this.competitionTimeExtraItem = null;
        if (this.countDownTimer != null)
        {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }
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
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                Observable.combineLatest(
                        providerCache.get(providerId)
                                .map(new PairGetSecond<ProviderId, ProviderDTO>())
                                .startWith(providerDTO != null ? Observable.just(providerDTO) : Observable.<ProviderDTO>empty()),
                        competitionCacheRx.get(competitionId)
                                .map(new PairGetSecond<CompetitionId, CompetitionDTO>())
                                .startWith(competitionDTO != null ? Observable.just(competitionDTO) : Observable.<CompetitionDTO>empty()),
                        competitionLeaderboardCache.get(competitionLeaderboardId)
                                .map(new PairGetSecond<CompetitionLeaderboardId, CompetitionLeaderboardDTO>())
                                .startWith(competitionLeaderboardDTO != null ? Observable.just(competitionLeaderboardDTO)
                                        : Observable.<CompetitionLeaderboardDTO>empty()),
                        new Func3<ProviderDTO, CompetitionDTO, CompetitionLeaderboardDTO, CompetitionLeaderboardDTO>()
                        {
                            @Override public CompetitionLeaderboardDTO call(ProviderDTO providerDTO,
                                    CompetitionDTO competitionDTO,
                                    CompetitionLeaderboardDTO competitionLeaderboardDTO)
                            {
                                linkWith(providerDTO, competitionDTO, competitionLeaderboardDTO);
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
                }, new ToastOnErrorAction1()));
    }

    protected void linkWith(ProviderDTO providerDTO, CompetitionDTO competitionDTO, @NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        this.providerDTO = providerDTO;
        this.competitionDTO = competitionDTO;
        this.competitionLeaderboardDTO = competitionLeaderboardDTO;
    }

    @Override protected void onNext(@NonNull PagedLeaderboardKey key, @NonNull LeaderboardMarkedUserItemDisplayDto.DTOList value)
    {
        super.onNext(key, value);
        addExtraTiles();
    }

    private void addExtraTiles()
    {
        if (competitionDTO != null && competitionDTO.leaderboard != null && competitionDTO.leaderboard.isWithinUtcRestricted())
        {
            //Add timer to the top of the recycler fragment.
            if (competitionTimeExtraItem == null)
            {
                competitionTimeExtraItem = new CompetitionTimeExtraItem(competitionDTO.leaderboard.toUtcRestricted);
                competitionAdapter.addExtraItem(0, competitionTimeExtraItem);
                long diffInMillies = competitionTimeExtraItem.until.getTime() - new Date().getTime();
                countDownTimer = new CountDownTimer(diffInMillies, 1000)
                {
                    @Override public void onTick(long millisUntilFinished)
                    {
                        if (competitionTimeExtraItem != null)
                        {
                            competitionTimeExtraItem.updateDate(new Date());
                            competitionAdapter.notifyItemChanged(0);
                        }
                    }

                    @Override public void onFinish()
                    {

                    }
                }.start();
            }
        }
        if (competitionLeaderboardDTO != null && competitionLeaderboardDTO.ads != null)
        {
            int realSize = itemViewAdapter.getItemCount();
            for (int i = competitionLeaderboardDTO.adStartRow; i < realSize; i += competitionLeaderboardDTO.adFrequencyRows)
            {
                int randomAds = (int) (Math.random() * competitionLeaderboardDTO.ads.size());
                AdDTO adDTO = competitionLeaderboardDTO.ads.get(randomAds);
                adDTO.providerId = providerDTO.id;
                competitionAdapter.addExtraItem(i, new CompetitionAdsExtraItem(getResources(), adDTO));
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
                        return new CompetitionLeaderboardItemDisplayDTO.Requisite(
                                requisite,
                                providerPair,
                                competitionLeaderboardPair);
                    }
                });
    }

    @Override protected LeaderboardMarkedUserItemDisplayDto createLoadingOwnRankingDTO()
    {
        return new CompetitionLeaderboardOwnRankingDisplayDTO(getResources(), currentUserId);
    }

    @Override protected LeaderboardMarkedUserItemDisplayDto createNotRankedOwnRankingDTO(LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        if (requisite instanceof CompetitionLeaderboardItemDisplayDTO.Requisite)
        {
            CompetitionLeaderboardItemDisplayDTO.Requisite thisRequisite = (CompetitionLeaderboardItemDisplayDTO.Requisite) requisite;
            if (requisite.currentLeaderboardUserDTO == null)
            {
                return new CompetitionLeaderboardOwnRankingDisplayDTO(getResources(), currentUserId,
                        requisite.currentUserProfileDTO, thisRequisite.providerDTO,
                        ((CompetitionLeaderboardItemDisplayDTO.Requisite) requisite).capAt);
            }
        }
        return super.createNotRankedOwnRankingDTO(requisite);
    }

    @Override protected LeaderboardMarkedUserItemDisplayDto createRankedOwnRankingDTO(LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        if (requisite instanceof CompetitionLeaderboardItemDisplayDTO.Requisite)
        {
            CompetitionLeaderboardItemDisplayDTO.Requisite thisRequisite = (CompetitionLeaderboardItemDisplayDTO.Requisite) requisite;
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
        WebViewIntentFragment.putUrl(args, providerUtil.getWizardPage(providerId) + "&previous=whatever");
        WebViewIntentFragment.putIsOptionMenuVisible(args, false);
        if (navigator != null)
        {
            this.webViewFragment = navigator.get().pushFragment(WebViewIntentFragment.class, args);
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

        @Override protected WebViewIntentFragment getApplicableWebViewFragment()
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
