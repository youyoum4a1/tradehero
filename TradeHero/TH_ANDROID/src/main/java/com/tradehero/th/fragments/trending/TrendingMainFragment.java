package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.position.FXMainPositionListFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.route.THRouter;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "trending-securities",
        "trending-stocks/tab-index/:stockPageIndex",
        "trending-stocks/exchange/:exchangeId",
        "trending-fx/tab-index/:fxPageIndex",
})
public class TrendingMainFragment extends DashboardFragment
{
    private static final String KEY_ASSET_CLASS = TrendingMainFragment.class.getName() + ".assetClass";
    private static final String KEY_EXCHANGE_ID = TrendingMainFragment.class.getName() + ".exchangeId";

    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject THRouter thRouter;
    @RouteProperty("stockPageIndex") Integer selectedStockPageIndex;
    @RouteProperty("fxPageIndex") Integer selectedFxPageIndex;
    @RouteProperty("exchangeId") Integer routedExchangeId;

    @NonNull private static TrendingTabType lastType = TrendingTabType.STOCK;
    @NonNull private static TrendingStockTabType lastStockTab = TrendingStockTabType.getDefault();
    @NonNull private static TrendingFXTabType lastFXTab = TrendingFXTabType.getDefault();

    private TradingStockPagerAdapter tradingStockPagerAdapter;
    private TradingFXPagerAdapter tradingFXPagerAdapter;
    private boolean fetchedFXPortfolio = false;
    private Observable<UserProfileDTO> userProfileObservable;
    @Nullable private OwnedPortfolioId fxPortfolioId;
    public static boolean fxDialogShowed = false;

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("trending-fx/my-fx", "trending-fx/tab-index/" + TrendingFXTabType.Portfolio.ordinal());
        router.registerAlias("trending-fx/trade-fx", "trending-fx/tab-index/" + TrendingFXTabType.FX.ordinal());
        router.registerAlias("trending-stocks/my-stocks", "trending-stocks/tab-index/" + TrendingStockTabType.StocksMain.ordinal());
        router.registerAlias("trending-stocks/favorites", "trending-stocks/tab-index/" + TrendingStockTabType.Favorites.ordinal());
        router.registerAlias("trending-stocks/trending", "trending-stocks/tab-index/" + TrendingStockTabType.Trending.ordinal());
        router.registerAlias("trending-stocks/price-action", "trending-stocks/tab-index/" + TrendingStockTabType.Price.ordinal());
        router.registerAlias("trending-stocks/unusual-volumes", "trending-stocks/tab-index/" + TrendingStockTabType.Volume.ordinal());
        router.registerAlias("trending-stocks/all-trending", "trending-stocks/tab-index/" + TrendingStockTabType.All.ordinal());
    }

    public static void putAssetClass(@NonNull Bundle args, @NonNull AssetClass assetClass)
    {
        args.putInt(KEY_ASSET_CLASS, assetClass.getValue());
    }

    @Nullable private static AssetClass getAssetClass(@NonNull Bundle args)
    {
        if (!args.containsKey(KEY_ASSET_CLASS))
        {
            return null;
        }
        return AssetClass.create(args.getInt(KEY_ASSET_CLASS));
    }

    public static void putExchangeId(@NonNull Bundle args, @NonNull ExchangeIntegerId exchangeId)
    {
        args.putBundle(KEY_EXCHANGE_ID, exchangeId.getArgs());
    }

    @Nullable private static ExchangeIntegerId getExchangeId(@NonNull Bundle args)
    {
        if (!args.containsKey(KEY_EXCHANGE_ID))
        {
            return null;
        }
        return new ExchangeIntegerId(args.getBundle(KEY_EXCHANGE_ID));
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        initUserProfileObservable();
    }

    private void initUserProfileObservable()
    {
        userProfileObservable = userProfileCache.getOne(currentUserId.toUserBaseKey())
                .subscribeOn(Schedulers.computation())
                .map(new Func1<Pair<UserBaseKey, UserProfileDTO>, UserProfileDTO>()
                {
                    @Override public UserProfileDTO call(Pair<UserBaseKey, UserProfileDTO> pair)
                    {
                        fetchedFXPortfolio = true;
                        if (pair.second.fxPortfolio == null)
                        {
                            fxPortfolioId = null;
                        }
                        else
                        {
                            fxPortfolioId = pair.second.fxPortfolio.getOwnedPortfolioId();
                        }
                        return pair.second;
                    }
                })
                .cache(1);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getArguments().remove(KEY_EXCHANGE_ID);
        tradingStockPagerAdapter = new TradingStockPagerAdapter(getChildFragmentManager());
        tradingFXPagerAdapter = new TradingFXPagerAdapter(getChildFragmentManager());
        AssetClass askedAssetClass = getAssetClass(getArguments());
        if (askedAssetClass != null)
        {
            try
            {
                lastType = TrendingTabType.getForAssetClass(askedAssetClass);
            } catch (IllegalArgumentException e)
            {
                Timber.e(e, "Unhandled assetClass for user " + currentUserId.get());
            }
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.trending_main_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int i, float v, int i2)
            {
            }

            @Override public void onPageSelected(int i)
            {
                switch (lastType)
                {
                    case STOCK:
                        lastStockTab = TrendingStockTabType.values()[i];
                        break;
                    case FX:
                        lastFXTab = TrendingFXTabType.values()[i];
                        break;
                    default:
                        throw new RuntimeException("Unhandled TrendingTabType." + lastType);
                }
            }

            @Override public void onPageScrollStateChanged(int i)
            {
            }
        });

        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                                initViews();
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    private void initViews()
    {
        if (fxPortfolioId == null)
        {
            lastType = TrendingTabType.STOCK;
        }
        tabViewPager.setAdapter(lastType.equals(TrendingTabType.STOCK) ? tradingStockPagerAdapter : tradingFXPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setDistributeEvenly(!lastType.equals(TrendingTabType.STOCK));
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
        handleRouting();
    }

    @Override public void onResume()
    {
        super.onResume();
        showToolbarSpinner();
        thRouter.inject(this, getArguments());
        handleRouting();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle("");
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, final int position, long id)
            {
                final TrendingTabType oldType = lastType;

                final ProgressDialog progressDialog;
                if (!fetchedFXPortfolio && userProfileCache.getCachedValue(currentUserId.toUserBaseKey()) == null)
                {
                    progressDialog =
                            ProgressDialog.show(getActivity(), getString(R.string.loading_loading), getString(R.string.alert_dialog_please_wait));
                    progressDialog.setCanceledOnTouchOutside(true);
                }
                else
                {
                    progressDialog = null;
                }
                Action0 dismissProgress = new DismissDialogAction0(progressDialog);
                // We want to identify whether to:
                // - wait for enough info
                // - pop for FX enroll
                // - just change the tab

                if (userProfileObservable == null)
                {
                    initUserProfileObservable();
                }
                onDestroyOptionsMenuSubscriptions.add(AppObservable.bindFragment(
                        TrendingMainFragment.this,
                        userProfileObservable)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnUnsubscribe(dismissProgress)
                        .finallyDo(dismissProgress)
                        .subscribe(new Subscriber<UserProfileDTO>()
                        {
                            @Override public void onCompleted()
                            {

                            }

                            @Override public void onError(Throwable e)
                            {
                                THToast.show(getString(R.string.error_fetch_your_user_profile));
                            }

                            @Override public void onNext(UserProfileDTO userProfileDTO)
                            {
                                if (position == 1 && userProfileDTO.fxPortfolio == null && fxPortfolioId == null)
                                {
                                    if (fxDialogShowed)
                                    {
                                        return;
                                    }
                                    else
                                    {
                                        fxDialogShowed = true;
                                    }
                                    final FxOnBoardDialogFragment onBoardDialogFragment =
                                            FxOnBoardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
                                    onBoardDialogFragment.getUserActionTypeObservable()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    new Action1<FxOnBoardDialogFragment.UserAction>()
                                                    {
                                                        @Override public void call(FxOnBoardDialogFragment.UserAction action)
                                                        {
                                                            handleUserEnrolledFX(action);
                                                        }
                                                    },
                                                    new TimberOnErrorAction("")
                                            );
                                }
                                else
                                {
                                    if (position == 0)
                                    {
                                        lastType = TrendingTabType.STOCK;
                                    }
                                    else
                                    {
                                        lastType = TrendingTabType.FX;
                                    }
                                    if (!oldType.equals(lastType))
                                    {
                                        clearChildFragmentManager();
                                        initViews();
                                    }
                                }
                            }
                        }));
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
                //do nothing
            }
        };
        configureDefaultSpinner(new String[] {
                        getString(R.string.stocks),
                        getString(R.string.fx)},
                listener, lastType.ordinal());
        handleRouting();
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return true;
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
        //Specific for this fragment.
        pagerSlidingTabStrip.setBackgroundColor(getResources().getColor(isLive ? R.color.tradehero_dark_red : R.color.tradehero_dark_blue));
    }

    @Override public void onPause()
    {
        hideToolbarSpinner();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.tradingStockPagerAdapter = null;
        this.tradingFXPagerAdapter = null;
        super.onDestroy();
    }

    @Override public void onDetach()
    {
        userProfileObservable = null;
        super.onDetach();
    }

    private class TradingStockPagerAdapter extends FragmentPagerAdapter
    {
        @NonNull final SparseArray<Fragment> registeredFragments;

        public TradingStockPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
            registeredFragments = new SparseArray<>();
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            TrendingStockTabType tabType = TrendingStockTabType.values()[position];
            Class fragmentClass = tabType.fragmentClass;
            TrendingStockFragment.putTabType(args, tabType);
            Fragment created = Fragment.instantiate(getActivity(), fragmentClass.getName(), args);
            registeredFragments.put(position, created);
            return created;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(TrendingStockTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return TrendingStockTabType.values().length;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }
    }

    private class TradingFXPagerAdapter extends FragmentPagerAdapter
    {
        public TradingFXPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            Class fragmentClass = TrendingFXTabType.values()[position].fragmentClass;
            if (fragmentClass.equals((Class) FXMainPositionListFragment.class))
            {
                FXMainPositionListFragment.putMainFXPortfolioId(args, fxPortfolioId);
            }
            return Fragment.instantiate(getActivity(), fragmentClass.getName(), args);
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(TrendingFXTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return TrendingFXTabType.values().length;
        }
    }

    protected void handleUserEnrolledFX(@NonNull FxOnBoardDialogFragment.UserAction userAction)
    {
        if (userAction.type.equals(FxOnBoardDialogFragment.UserActionType.ENROLLED))
        {
            //noinspection ConstantConditions
            fxPortfolioId = userAction.created.getOwnedPortfolioId();
            if (fxPortfolioId != null)
            {
                userProfileCache.invalidate(currentUserId.toUserBaseKey());
            }
            lastType = TrendingTabType.FX;
        }
        else
        {
            lastType = TrendingTabType.STOCK;
        }
        clearChildFragmentManager();
        initViews();
    }

    protected void clearChildFragmentManager()
    {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null)
        {
            fragments.clear();
        }
    }

    protected void handleRouting()
    {
        if (selectedStockPageIndex != null)
        {
            if (lastType.equals(TrendingTabType.STOCK))
            {
                if (tabViewPager != null)
                {
                    lastStockTab = TrendingStockTabType.values()[selectedStockPageIndex];
                    tabViewPager.setCurrentItem(selectedStockPageIndex, true);
                    selectedStockPageIndex = null;
                }
            }
            else if (actionBarOwnerMixin != null)
            {
                lastType = TrendingTabType.STOCK;
                actionBarOwnerMixin.setSpinnerSelection(TrendingTabType.STOCK.ordinal());
            }
        }
        else if (selectedFxPageIndex != null)
        {
            if (lastType.equals(TrendingTabType.FX))
            {
                if (tabViewPager != null)
                {
                    lastFXTab = TrendingFXTabType.values()[selectedFxPageIndex];
                    tabViewPager.setCurrentItem(selectedFxPageIndex, true);
                    selectedFxPageIndex = null;
                }
            }
            else if (actionBarOwnerMixin != null)
            {
                lastType = TrendingTabType.FX;
                actionBarOwnerMixin.setSpinnerSelection(TrendingTabType.FX.ordinal());
            }
        }
        else if (lastType.equals(TrendingTabType.STOCK))
        {
            tabViewPager.setCurrentItem(lastStockTab.ordinal(), true);
        }
        else if (lastType.equals(TrendingTabType.FX))
        {
            tabViewPager.setCurrentItem(lastFXTab.ordinal(), true);
        }
        else
        {
            throw new RuntimeException("Unhandled TrendingTabType." + lastType);
        }

        if (routedExchangeId != null
                && tabViewPager != null
                && lastType.equals(TrendingTabType.STOCK))
        {
            Fragment currentFragment = tradingStockPagerAdapter.registeredFragments.get(tabViewPager.getCurrentItem());
            if (currentFragment instanceof TrendingStockFragment)
            {
                ((TrendingStockFragment) currentFragment).setExchangeByCode(routedExchangeId);
                routedExchangeId = null;
            }
        }
    }

    public static void setLastType(@NonNull AssetClass assetClass)
    {
        if (assetClass.equals(AssetClass.STOCKS))
        {
            lastType = TrendingTabType.STOCK;
            lastStockTab = TrendingStockTabType.getDefault();
        }
        else if (assetClass.equals(AssetClass.FX))
        {
            lastType = TrendingTabType.FX;
            lastFXTab = TrendingFXTabType.getDefault();
        }
    }
}
