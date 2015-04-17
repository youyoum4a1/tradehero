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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefDTOList;
import com.tradehero.th.api.games.ViralMiniGameDefListKey;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.games.ViralGamePopupDialogFragment;
import com.tradehero.th.fragments.position.FXMainPositionListFragment;
import com.tradehero.th.persistence.games.ViralMiniGameDefListCache;
import com.tradehero.th.persistence.prefs.ShowViralGameDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable("trending-securities")
public class TrendingMainFragment extends DashboardFragment
{
    private static final String KEY_ASSET_CLASS = TrendingMainFragment.class.getName() + ".assetClass";
    private static final String KEY_EXCHANGE_ID = TrendingMainFragment.class.getName() + ".exchangeId";

    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;
    @Inject @ShowViralGameDialog TimingIntervalPreference showViralGameTimingIntervalPreference;
    @Inject Lazy<ViralMiniGameDefListCache> viralMiniGameDefListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    @NonNull private static TrendingTabType lastType = TrendingTabType.STOCK;
    private static int lastPosition = 1;

    private TradingStockPagerAdapter tradingStockPagerAdapter;
    private TradingFXPagerAdapter tradingFXPagerAdapter;
    private Subscription viralSubscription;
    private boolean fetchedFXPortfolio = false;
    private Observable<UserProfileDTO> userProfileObservable;
    @Nullable private OwnedPortfolioId fxPortfolioId;

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
                lastPosition = i;
            }

            @Override public void onPageScrollStateChanged(int i)
            {
            }
        });
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions.add(AppObservable.bindFragment(
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
        tabViewPager.setAdapter(lastType.equals(TrendingTabType.STOCK) ? tradingStockPagerAdapter : tradingFXPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }
        pagerSlidingTabStrip.setCustomTabView(lastType.equals(TrendingTabType.STOCK) ? R.layout.th_page_indicator : R.layout.th_tab_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
        tabViewPager.setCurrentItem(lastPosition, true);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (showViralGameTimingIntervalPreference.isItTime())
        {
            viralSubscription = AppObservable.bindFragment(this, viralMiniGameDefListCache.get().get(new ViralMiniGameDefListKey()))
                    .take(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<ViralMiniGameDefListKey, ViralMiniGameDefDTOList>>()
                    {
                        @Override public void onCompleted()
                        {
                            // Do nothing.
                        }

                        @Override public void onError(Throwable e)
                        {
                            // Do nothing.
                        }

                        @Override public void onNext(
                                Pair<ViralMiniGameDefListKey, ViralMiniGameDefDTOList> viralMiniGameDefListKeyViralMiniGameDefDTOListPair)
                        {
                            ViralMiniGameDefDTO viralMiniGameDefDTO =
                                    viralMiniGameDefListKeyViralMiniGameDefDTOListPair.second.getRandomViralMiniGameDefDTO();
                            if (viralMiniGameDefDTO != null)
                            {
                                ViralGamePopupDialogFragment f = ViralGamePopupDialogFragment.newInstance(viralMiniGameDefDTO.getDTOKey(), true);
                                f.show(getChildFragmentManager(), ViralGamePopupDialogFragment.class.getName());
                            }
                        }
                    });
        }
        showToolbarSpinner();
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
                final Action1<UserProfileDTO> effectTabChangeAction = new Action1<UserProfileDTO>()
                {
                    @Override public void call(UserProfileDTO userProfileDTO)
                    {
                        if (position == 1 && userProfileDTO.fxPortfolio == null && fxPortfolioId == null)
                        {
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
                                lastPosition = 1;
                                clearChildFragmentManager();
                                initViews();
                            }
                        }
                    }
                };

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
                onStopSubscriptions.add(AppObservable.bindFragment(
                        TrendingMainFragment.this,
                        userProfileObservable)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnUnsubscribe(dismissProgress)
                        .finallyDo(dismissProgress)
                        .subscribe(
                                effectTabChangeAction,
                                new ToastOnErrorAction(getString(R.string.error_fetch_your_user_profile))));
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
    }

    @Override public void onPause()
    {
        unsubscribe(viralSubscription);
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
        public TradingStockPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            Class fragmentClass = TrendingStockTabType.values()[position].fragmentClass;
            args.putInt(TrendingStockFragment.KEY_TYPE_ID, position);
            return Fragment.instantiate(getActivity(), fragmentClass.getName(), args);
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(TrendingStockTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return TrendingStockTabType.values().length;
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
            lastType = TrendingTabType.FX;
        }
        else
        {
            lastType = TrendingTabType.STOCK;
        }
        lastPosition = 0;
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

    public static void setLastType(@NonNull AssetClass assetClass)
    {
        if (assetClass.equals(AssetClass.STOCKS))
        {
            lastType = TrendingTabType.STOCK;
        }
        else if (assetClass.equals(AssetClass.FX))
        {
            lastType = TrendingTabType.FX;
        }
    }

    public static void setLastPosition(int lastPosition)
    {
        TrendingMainFragment.lastPosition = lastPosition;
    }
}
