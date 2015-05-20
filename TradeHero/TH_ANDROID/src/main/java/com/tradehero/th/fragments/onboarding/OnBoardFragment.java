package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactSectorListDTO;
import com.tradehero.th.api.market.MarketRegion;
import com.tradehero.th.api.market.SectorCompactDTOList;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityIntegerIdListForm;
import com.tradehero.th.api.social.BatchFollowFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.onboarding.exchange.ExchangeSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.hero.UserSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.last.OnBoardLastFragment;
import com.tradehero.th.fragments.onboarding.sector.SectorSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.stock.StockSelectionScreenFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.prefs.FirstShowOnBoardDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.rx.TimberAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.viewpagerindicator.PageIndicator;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.Subscriptions;

public class OnBoardFragment extends BaseFragment
{
    private static final int INDEX_SELECTION_EXCHANGES = 0;
    private static final int INDEX_SELECTION_SECTORS = 1;
    private static final int INDEX_SELECTION_HEROES = 2;
    private static final int INDEX_SELECTION_WATCHLIST = 3;
    private static final int INDEX_SELECTION_LAST = 4;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject @FirstShowOnBoardDialog TimingIntervalPreference firstShowOnBoardDialogPreference;
    @Inject BroadcastUtils broadcastUtils;

    @InjectView(android.R.id.content) ViewPager pager;
    @InjectView(R.id.page_indicator) PageIndicator pageIndicator;

    private MarketRegion selectedRegion;
    private boolean hadAutoSelectedExchange;
    private ExchangeCompactDTOList selectedExchanges;
    private boolean hadAutoSelectedSector;
    private SectorCompactDTOList selectedSectors;
    private LeaderboardUserDTOList selectedHeroes;
    private SecurityCompactDTOList selectedStocks;
    @NonNull private final Subscription[] fragmentSubscriptions;
    @NonNull private final BehaviorSubject<ExchangeCompactDTOList> selectedExchangesSubject;
    @NonNull private final BehaviorSubject<SectorCompactDTOList> selectedSectorsSubject;
    @NonNull private BehaviorSubject<SecurityCompactDTOList> selectedSecuritiesBehavior;

    public OnBoardFragment()
    {
        fragmentSubscriptions = new Subscription[getTabCount()];
        selectedExchangesSubject = BehaviorSubject.create();
        selectedSectorsSubject = BehaviorSubject.create();
        selectedSecuritiesBehavior = BehaviorSubject.create();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.on_board_fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        pager.canScrollHorizontally(-1);
        pager.setCurrentItem(getCurrentIndex());
        pageIndicator.setViewPager(pager, getCurrentIndex());
        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Nullable Integer errorMessage;

            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                errorMessage = getTabAllowedErrorMessage(position);
                if (errorMessage != null)
                {
                    pager.setCurrentItem(position - 1);
                }
            }

            @Override public void onPageSelected(int position)
            {
                Integer newErrorMessage = getTabAllowedErrorMessage(position);
                if (newErrorMessage != null)
                {
                    errorMessage = newErrorMessage;
                }
                if (errorMessage != null)
                {
                    THToast.show(errorMessage);
                    errorMessage = null;
                    pager.setCurrentItem(position - 1);
                }
                else if (position == INDEX_SELECTION_LAST)
                {
                    firstShowOnBoardDialogPreference.justHandled();
                    submitActions();
                }
            }

            @Override public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        broadcastUtils.nextPlease();
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.closeButton)
    protected void closeButtonClicked(View view)
    {
        firstShowOnBoardDialogPreference.justHandled();
        getActivity().finish();
    }

    private int getCurrentIndex()
    {
        int index;
        if (selectedExchanges == null || selectedExchanges.size() == 0)
        {
            index = INDEX_SELECTION_EXCHANGES;
        }
        else if (selectedSectors == null || selectedSectors.size() == 0)
        {
            index = INDEX_SELECTION_SECTORS;
        }
        else if (selectedHeroes == null || selectedHeroes.size() == 0)
        {
            index = INDEX_SELECTION_HEROES;
        }
        else if (selectedStocks == null || selectedStocks.size() == 0)
        {
            index = INDEX_SELECTION_WATCHLIST;
        }
        else
        {
            index = INDEX_SELECTION_LAST;
        }
        return index;
    }

    @Nullable @StringRes protected Integer getTabAllowedErrorMessage(int position)
    {
        if (INDEX_SELECTION_EXCHANGES < position && (selectedExchanges == null || selectedExchanges.size() == 0))
        {
            return R.string.on_board_exchange_need_to_select;
        }
        if (INDEX_SELECTION_SECTORS < position && (selectedSectors == null || selectedSectors.size() == 0))
        {
            return R.string.on_board_sector_need_to_select;
        }
        if (INDEX_SELECTION_HEROES < position && (selectedHeroes == null || selectedHeroes.size() == 0))
        {
            return R.string.on_board_user_need_to_select;
        }
        if (INDEX_SELECTION_WATCHLIST < position && (selectedStocks == null || selectedStocks.size() == 0))
        {
            return R.string.on_board_stock_need_to_select;
        }
        return null;
    }

    private static int getTabCount()
    {
        return INDEX_SELECTION_LAST + 1;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter
    {
        PagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public int getCount()
        {
            return getTabCount();
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyTouchHome(args, false);
            switch (position)
            {
                case INDEX_SELECTION_EXCHANGES:
                    return getExchangeSelectionFragment(args);

                case INDEX_SELECTION_SECTORS:
                    return getSectorSelectionFragment(args);

                case INDEX_SELECTION_HEROES:
                    return getHeroSelectionFragment(args);

                case INDEX_SELECTION_WATCHLIST:
                    return getStockSelectionFragment(args);

                case INDEX_SELECTION_LAST:
                    return getLastFragment(args);
            }
            throw new IllegalArgumentException("Unknown position " + position);
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            fragmentSubscriptions[position].unsubscribe();
            super.destroyItem(container, position, object);
        }
    }

    @NonNull private Fragment getExchangeSelectionFragment(@NonNull Bundle args)
    {
        final ExchangeSelectionScreenFragment fragment = new ExchangeSelectionScreenFragment();
        ExchangeSelectionScreenFragment.putRequisites(args,
                selectedRegion,
                hadAutoSelectedExchange,
                selectedExchanges != null ? selectedExchanges.getExchangeIds() : null);
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_EXCHANGES] = fragment.getMarketRegionClickedObservable()
                .subscribeOn(Schedulers.computation())
                .startWith(selectedRegion)
                .flatMap(new Func1<MarketRegion, Observable<ExchangeCompactDTOList>>()
                {
                    @Override public Observable<ExchangeCompactDTOList> call(MarketRegion marketRegion)
                    {
                        selectedRegion = marketRegion;
                        hadAutoSelectedExchange = marketRegion != null;
                        return marketRegion == null
                                ? Observable.<ExchangeCompactDTOList>empty()
                                : fragment.getSelectedExchangesObservable()
                                        .subscribeOn(Schedulers.computation())
                                        .startWith(selectedExchanges == null ? new ExchangeCompactDTOList() : selectedExchanges);
                    }
                })
                .flatMap(new Func1<ExchangeCompactDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final ExchangeCompactDTOList exchangeDTOs)
                    {
                        selectedExchanges = exchangeDTOs;
                        if (exchangeDTOs.size() > 0)
                        {
                            selectedExchangesSubject.onNext(exchangeDTOs);
                        }
                        return fragment.getNextClickedObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean forward)
                                    {
                                        if (forward && exchangeDTOs.size() > 0)
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_SECTORS, true);
                                        }
                                    }
                                });
                    }
                })
                .subscribe(
                        new TimberAction1<Boolean>("Moved to sectors page"),
                        new TimberOnErrorAction("Failed to collect exchange compacts"));
        return fragment;
    }

    @NonNull private Fragment getSectorSelectionFragment(@NonNull Bundle args)
    {
        final SectorSelectionScreenFragment fragment = new SectorSelectionScreenFragment();
        SectorSelectionScreenFragment.putRequisites(args,
                hadAutoSelectedSector,
                selectedSectors != null ? selectedSectors.getSectorIds() : null);
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_SECTORS] = fragment.getSelectedSectorsObservable()
                .subscribeOn(Schedulers.computation())
                .startWith(selectedSectors == null ? new SectorCompactDTOList() : selectedSectors)
                .flatMap(new Func1<SectorCompactDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final SectorCompactDTOList sectorDTOs)
                    {
                        selectedSectors = sectorDTOs;
                        if (sectorDTOs.size() > 0)
                        {
                            hadAutoSelectedSector = true;
                            selectedSectorsSubject.onNext(sectorDTOs);
                        }
                        return fragment.getNextClickedObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean forward)
                                    {
                                        if (!forward)
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_EXCHANGES, true);
                                        }
                                        else if (sectorDTOs.size() > 0)
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_HEROES, true);
                                        }
                                    }
                                });
                    }
                })
                .subscribe(
                        new TimberAction1<Boolean>("Sector selection button"),
                        new TimberOnErrorAction("Failed to collect sectors"));
        return fragment;
    }

    @NonNull private Fragment getHeroSelectionFragment(@NonNull Bundle args)
    {
        final UserSelectionScreenFragment fragment = new UserSelectionScreenFragment();
        fragment.setSelectedExchangesSectorsObservable(getExchangeCompactSectorObservable());
        if (selectedHeroes != null)
        {
            UserSelectionScreenFragment.putInitialHeroes(args, selectedHeroes);
        }
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_HEROES] = fragment.getSelectedUsersObservable()
                .subscribeOn(Schedulers.computation())
                .startWith(new LeaderboardUserDTOList())
                .flatMap(new Func1<LeaderboardUserDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final LeaderboardUserDTOList leaderboardUserDTOs)
                    {
                        selectedHeroes = leaderboardUserDTOs;
                        return fragment.getNextClickedObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean forward)
                                    {
                                        if (!forward)
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_SECTORS, true);
                                        }
                                        else if (leaderboardUserDTOs.size() > 0)
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_WATCHLIST, true);
                                        }
                                    }
                                });
                    }
                })
                .subscribe(
                        new TimberAction1<Boolean>("Heroes selection button"),
                        new TimberOnErrorAction("Failed to collect heroes"));
        return fragment;
    }

    @NonNull private Fragment getStockSelectionFragment(@NonNull Bundle args)
    {
        final StockSelectionScreenFragment fragment = new StockSelectionScreenFragment();
        fragment.setSelectedExchangesSectorsObservable(getExchangeCompactSectorObservable());
        if (selectedStocks != null)
        {
            StockSelectionScreenFragment.putInitialStocks(args, selectedStocks.getSecurityIds());
        }
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_WATCHLIST] = fragment.getSelectedStocksObservable()
                .subscribeOn(Schedulers.computation())
                .startWith(new SecurityCompactDTOList())
                .flatMap(new Func1<SecurityCompactDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(SecurityCompactDTOList securityCompactDTOs)
                    {
                        selectedStocks = securityCompactDTOs;
                        selectedSecuritiesBehavior.onNext(securityCompactDTOs);
                        return fragment.getNextClickedObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean forward)
                                    {
                                        if (!forward)
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_HEROES, true);
                                        }
                                        else
                                        {
                                            pager.setCurrentItem(INDEX_SELECTION_LAST, true);
                                        }
                                    }
                                });
                    }
                })
                .subscribe(
                        new TimberAction1<Boolean>("Stocks selection button"),
                        new ToastAndLogOnErrorAction("Failed to get selected stocks"));
        return fragment;
    }

    @NonNull private Observable<ExchangeCompactSectorListDTO> getExchangeCompactSectorObservable()
    {
        return Observable.combineLatest(
                selectedExchangesSubject,
                selectedSectorsSubject,
                new Func2<ExchangeCompactDTOList, SectorCompactDTOList, ExchangeCompactSectorListDTO>()
                {
                    @Override
                    public ExchangeCompactSectorListDTO call(ExchangeCompactDTOList exchangeCompactDTOs, SectorCompactDTOList sectorCompactDTOs)
                    {
                        return new ExchangeCompactSectorListDTO(exchangeCompactDTOs, sectorCompactDTOs);
                    }
                });
    }

    @NonNull private Fragment getLastFragment(@NonNull Bundle args)
    {
        OnBoardLastFragment fragment = new OnBoardLastFragment();
        fragment.setSelectedSecuritiesObservable(selectedSecuritiesBehavior.asObservable());
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_LAST] = Subscriptions.create(new Action0()
        {
            @Override public void call()
            {
                // Nothing
            }
        });
        return fragment;
    }

    private void submitActions()
    {
        userServiceWrapper.followBatchFreeRx(new BatchFollowFormDTO(selectedHeroes, null))
                .subscribe(
                        new TimberAction1<UserProfileDTO>("Submitted selectedHeroes"),
                        new ToastAndLogOnErrorAction("Failed to submit selectedHeroes " + selectedHeroes));

        watchlistServiceWrapper.batchCreateRx(new SecurityIntegerIdListForm(selectedStocks, null))
                .subscribe(
                        new TimberAction1<WatchlistPositionDTOList>("Submitted selectedStocks"),
                        new ToastAndLogOnErrorAction("Failed to submit selectedStocks" + selectedStocks));
    }
}
