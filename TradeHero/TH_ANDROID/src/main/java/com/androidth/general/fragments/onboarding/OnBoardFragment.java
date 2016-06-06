package com.androidth.general.fragments.onboarding;

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
import butterknife.Bind;
import butterknife.OnClick;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.leaderboard.LeaderboardUserDTOList;
import com.androidth.general.api.market.ExchangeCompactDTOList;
import com.androidth.general.api.market.ExchangeCompactSectorListDTO;
import com.androidth.general.api.market.MarketRegion;
import com.androidth.general.api.market.SectorDTOList;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.SecurityIntegerIdListForm;
import com.androidth.general.api.social.BatchFollowFormDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.fragments.onboarding.exchange.ExchangeSelectionScreenFragment;
import com.androidth.general.fragments.onboarding.hero.UserSelectionScreenFragment;
import com.androidth.general.fragments.onboarding.last.OnBoardLastFragment;
import com.androidth.general.fragments.onboarding.sector.SectorSelectionScreenFragment;
import com.androidth.general.fragments.onboarding.stock.StockSelectionScreenFragment;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.network.service.WatchlistServiceWrapper;
import com.androidth.general.persistence.prefs.FirstShowOnBoardDialog;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import com.androidth.general.rx.TimberAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.utils.broadcast.BroadcastUtils;
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

    @Bind(android.R.id.content) ViewPager pager;
    @Bind(R.id.page_indicator) PageIndicator pageIndicator;

    private MarketRegion selectedRegion;
    private boolean hadAutoSelectedExchange;
    private ExchangeCompactDTOList selectedExchanges;
    private boolean hadAutoSelectedSector;
    private SectorDTOList selectedSectors;
    private LeaderboardUserDTOList selectedHeroes;
    private SecurityCompactDTOList selectedStocks;
    @NonNull private final BehaviorSubject<ExchangeCompactDTOList> selectedExchangesSubject;
    @NonNull private final BehaviorSubject<SectorDTOList> selectedSectorsSubject;
    @NonNull private BehaviorSubject<SecurityCompactDTOList> selectedSecuritiesBehavior;

    public OnBoardFragment()
    {
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
        ButterKnife.bind(this, view);
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
        ButterKnife.unbind(this);
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
        @NonNull private final Subscription[] fragmentSubscriptions;

        PagerAdapter(FragmentManager fm)
        {
            super(fm);
            fragmentSubscriptions = new Subscription[getTabCount()];
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
                    return getExchangeSelectionFragment(fragmentSubscriptions, args);

                case INDEX_SELECTION_SECTORS:
                    return getSectorSelectionFragment(fragmentSubscriptions, args);

                case INDEX_SELECTION_HEROES:
                    return getHeroSelectionFragment(fragmentSubscriptions, args);

                case INDEX_SELECTION_WATCHLIST:
                    return getStockSelectionFragment(fragmentSubscriptions, args);

                case INDEX_SELECTION_LAST:
                    return getLastFragment(fragmentSubscriptions, args);
            }
            throw new IllegalArgumentException("Unknown position " + position);
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            Subscription toForget = fragmentSubscriptions[position];
            if (toForget != null)
            {
                toForget.unsubscribe();
            }
            super.destroyItem(container, position, object);
        }
    }

    @NonNull private Fragment getExchangeSelectionFragment(@NonNull Subscription[] fragmentSubscriptions, @NonNull Bundle args)
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
                        new TimberOnErrorAction1("Failed to collect exchange compacts"));
        return fragment;
    }

    @NonNull private Fragment getSectorSelectionFragment(@NonNull Subscription[] fragmentSubscriptions, @NonNull Bundle args)
    {
        final SectorSelectionScreenFragment fragment = new SectorSelectionScreenFragment();
        SectorSelectionScreenFragment.putRequisites(args,
                hadAutoSelectedSector,
                selectedSectors != null ? selectedSectors.getSectorIds() : null);
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_SECTORS] = fragment.getSelectedSectorsObservable()
                .subscribeOn(Schedulers.computation())
                .startWith(selectedSectors == null ? new SectorDTOList() : selectedSectors)
                .flatMap(new Func1<SectorDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final SectorDTOList sectorDTOs)
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
                        new TimberOnErrorAction1("Failed to collect sectors"));
        return fragment;
    }

    @NonNull private Fragment getHeroSelectionFragment(@NonNull Subscription[] fragmentSubscriptions, @NonNull Bundle args)
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
                        new TimberOnErrorAction1("Failed to collect heroes"));
        return fragment;
    }

    @NonNull private Fragment getStockSelectionFragment(@NonNull Subscription[] fragmentSubscriptions, @NonNull Bundle args)
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
                        new TimberAndToastOnErrorAction1("Failed to get selected stocks"));
        return fragment;
    }

    @NonNull private Observable<ExchangeCompactSectorListDTO> getExchangeCompactSectorObservable()
    {
        return Observable.combineLatest(
                selectedExchangesSubject,
                selectedSectorsSubject,
                new Func2<ExchangeCompactDTOList, SectorDTOList, ExchangeCompactSectorListDTO>()
                {
                    @Override
                    public ExchangeCompactSectorListDTO call(ExchangeCompactDTOList exchangeCompactDTOs, SectorDTOList sectorCompactDTOs)
                    {
                        return new ExchangeCompactSectorListDTO(exchangeCompactDTOs, sectorCompactDTOs);
                    }
                });
    }

    @NonNull private Fragment getLastFragment(@NonNull Subscription[] fragmentSubscriptions, @NonNull Bundle args)
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
                        new TimberAndToastOnErrorAction1("Failed to submit selectedHeroes " + selectedHeroes));

        watchlistServiceWrapper.batchCreateRx(new SecurityIntegerIdListForm(selectedStocks, null))
                .subscribe(
                        new TimberAction1<WatchlistPositionDTOList>("Submitted selectedStocks"),
                        new TimberAndToastOnErrorAction1("Failed to submit selectedStocks" + selectedStocks));
    }
}
