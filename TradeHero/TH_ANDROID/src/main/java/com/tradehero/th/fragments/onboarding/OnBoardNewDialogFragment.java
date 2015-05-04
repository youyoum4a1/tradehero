package com.tradehero.th.fragments.onboarding;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
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
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerIdListForm;
import com.tradehero.th.api.social.BatchFollowFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.BaseDialogSupportFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.onboarding.exchange.ExchangeSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.hero.UserSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.last.OnBoardLastFragment;
import com.tradehero.th.fragments.onboarding.sector.SectorSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.stock.StockSelectionScreenFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.prefs.FirstShowOnBoardDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.route.THRouter;
import com.viewpagerindicator.PageIndicator;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class OnBoardNewDialogFragment extends BaseDialogSupportFragment
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
    @Inject THRouter thRouter;

    @InjectView(android.R.id.content) ViewPager pager;
    @InjectView(R.id.page_indicator) PageIndicator pageIndicator;

    private MarketRegion selectedRegion;
    private ExchangeCompactDTOList selectedExchanges;
    private SectorCompactDTOList selectedSectors;
    private LeaderboardUserDTOList selectedHeroes;
    private SecurityCompactDTOList selectedStocks;
    @NonNull private final Subscription[] fragmentSubscriptions;
    @NonNull private BehaviorSubject<ExchangeCompactSectorListDTO> exchangeSectorBehavior;
    @NonNull private BehaviorSubject<SecurityCompactDTOList> selectedSecuritiesBehavior;

    public static OnBoardNewDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        OnBoardNewDialogFragment dialogFragment = new OnBoardNewDialogFragment();
        dialogFragment.show(fragmentManager, OnBoardNewDialogFragment.class.getName());
        return dialogFragment;
    }

    public OnBoardNewDialogFragment()
    {
        fragmentSubscriptions = new Subscription[getTabCount()];
        exchangeSectorBehavior = BehaviorSubject.create();
        selectedSecuritiesBehavior = BehaviorSubject.create();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.on_board_new_dialog, container, false);
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
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                Integer errorMessage = getTabAllowedErrorMessage(position);
                if (errorMessage != null)
                {
                    THToast.show(errorMessage);
                    pager.setCurrentItem(position - 1);
                }
            }

            @Override public void onPageSelected(int position)
            {
                Integer errorMessage = getTabAllowedErrorMessage(position);
                if (errorMessage != null)
                {
                    THToast.show(errorMessage);
                    pager.setCurrentItem(position - 1);
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

    @OnClick(android.R.id.closeButton)
    @Override public void dismiss()
    {
        super.dismiss();
        firstShowOnBoardDialogPreference.justHandled();
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        broadcastUtils.nextPlease();
    }

    private int getCurrentIndex()
    {
        int index;
        if (selectedStocks != null)
        {
            index = INDEX_SELECTION_LAST;
        }
        else if (selectedHeroes != null)
        {
            index = INDEX_SELECTION_WATCHLIST;
        }
        else if (selectedSectors != null)
        {
            index = INDEX_SELECTION_HEROES;
        }
        else if (selectedExchanges != null)
        {
            index = INDEX_SELECTION_SECTORS;
        }
        else
        {
            index = INDEX_SELECTION_EXCHANGES;
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
        if (selectedRegion != null)
        {
            ExchangeSelectionScreenFragment.putInitialRegion(args, selectedRegion);
        }
        if (selectedExchanges != null)
        {
            ExchangeSelectionScreenFragment.putInitialExchanges(args, selectedExchanges.getExchangeIds());
        }
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_EXCHANGES] = fragment.getMarketRegionClickedObservable()
                .flatMap(new Func1<MarketRegion, Observable<ExchangeCompactDTOList>>()
                {
                    @Override public Observable<ExchangeCompactDTOList> call(MarketRegion marketRegion)
                    {
                        selectedRegion = marketRegion;
                        return fragment.getSelectedExchangesObservable();
                    }
                })
                .flatMap(new Func1<ExchangeCompactDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(ExchangeCompactDTOList exchangeDTOs)
                    {
                        selectedExchanges = exchangeDTOs;
                        return fragment.getNextClickedObservable();
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean clicked)
                            {
                                if (clicked)
                                {
                                    pager.setCurrentItem(INDEX_SELECTION_SECTORS, true);
                                }
                            }
                        },
                        new TimberOnErrorAction("Failed to collect exchange compacts"));
        return fragment;
    }

    @NonNull private Fragment getSectorSelectionFragment(@NonNull Bundle args)
    {
        final SectorSelectionScreenFragment fragment = new SectorSelectionScreenFragment();
        if (selectedSectors != null)
        {
            SectorSelectionScreenFragment.putInitialSectors(args, selectedSectors.getSectorIds());
        }
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_SECTORS] = fragment.getSelectedSectorsObservable()
                .flatMap(new Func1<SectorCompactDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(SectorCompactDTOList sectorDTOs)
                    {
                        selectedSectors = sectorDTOs;
                        if (sectorDTOs.size() > 0)
                        {
                            exchangeSectorBehavior.onNext(new ExchangeCompactSectorListDTO(selectedExchanges, sectorDTOs));
                        }
                        return fragment.getNextClickedObservable();
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean clicked)
                            {
                                if (clicked)
                                {
                                    pager.setCurrentItem(INDEX_SELECTION_HEROES, true);
                                }
                            }
                        },
                        new TimberOnErrorAction("Failed to collect sectors"));
        return fragment;
    }

    @NonNull private Fragment getHeroSelectionFragment(@NonNull Bundle args)
    {
        final UserSelectionScreenFragment fragment = new UserSelectionScreenFragment();
        fragment.setSelectedExchangesSectorsObservable(exchangeSectorBehavior.asObservable());
        if (selectedHeroes != null)
        {
            UserSelectionScreenFragment.putInitialHeroes(args, selectedHeroes);
        }
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_HEROES] = fragment.getSelectedUsersObservable()
                .flatMap(new Func1<LeaderboardUserDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(LeaderboardUserDTOList leaderboardUserDTOs)
                    {
                        selectedHeroes = leaderboardUserDTOs;
                        return fragment.getNextClickedObservable();
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean clicked)
                            {
                                if (clicked)
                                {
                                    pager.setCurrentItem(INDEX_SELECTION_WATCHLIST, true);
                                }
                            }
                        },
                        new TimberOnErrorAction("Failed to collect heroes"));
        return fragment;
    }

    @NonNull private Fragment getStockSelectionFragment(@NonNull Bundle args)
    {
        final StockSelectionScreenFragment fragment = new StockSelectionScreenFragment();
        fragment.setSelectedExchangesSectorsObservable(exchangeSectorBehavior.asObservable());
        if (selectedStocks != null)
        {
            StockSelectionScreenFragment.putInitialStocks(args, selectedStocks.getSecurityIds());
        }
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_WATCHLIST] = fragment.getSelectedStocksObservable()
                .flatMap(new Func1<SecurityCompactDTOList, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(SecurityCompactDTOList securityCompactDTOs)
                    {
                        selectedStocks = securityCompactDTOs;
                        selectedSecuritiesBehavior.onNext(securityCompactDTOs);
                        return fragment.getNextClickedObservable();
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean clicked)
                            {
                                if (clicked)
                                {
                                    pager.setCurrentItem(INDEX_SELECTION_LAST);
                                    submitActions();
                                    firstShowOnBoardDialogPreference.justHandled();
                                }
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to get selected stocks"));
        return fragment;
    }

    @NonNull private Fragment getLastFragment(@NonNull Bundle args)
    {
        OnBoardLastFragment fragment = new OnBoardLastFragment();
        fragment.setSelectedSecuritiesObservable(selectedSecuritiesBehavior.asObservable());
        fragment.setArguments(args);
        fragmentSubscriptions[INDEX_SELECTION_LAST] = fragment.getFragmentRequestedObservable()
                .subscribe(new Action1<Pair<SecurityId, Class<? extends DashboardFragment>>>()
                           {
                               @Override public void call(Pair<SecurityId, Class<? extends DashboardFragment>> pair)
                               {
                                   moveOnToFragment(pair);
                                   dismiss();
                               }
                           }
                );
        return fragment;
    }

    private void submitActions()
    {
        userServiceWrapper.followBatchFreeRx(new BatchFollowFormDTO(selectedHeroes, null))
                .subscribe(
                        new EmptyAction1<UserProfileDTO>(),
                        new ToastAndLogOnErrorAction("Failed to submit selectedHeroes " + selectedHeroes));

        watchlistServiceWrapper.batchCreateRx(new SecurityIntegerIdListForm(selectedStocks, null))
                .subscribe(
                        new EmptyAction1<WatchlistPositionDTOList>(),
                        new ToastAndLogOnErrorAction("Failed to submit selectedStocks" + selectedStocks));
    }

    private void moveOnToFragment(Pair<SecurityId, Class<? extends DashboardFragment>> pair)
    {
        if (pair.second.equals(BuySellStockFragment.class))
        {
            SecurityId securityId = pair.first;
            thRouter.open("stockSecurity/" + securityId.getExchange() + "/" + securityId.getSecuritySymbol(), getActivity());
        }
        else
        {
            thRouter.open("trendingstocks/1", getActivity());
        }
    }
}
