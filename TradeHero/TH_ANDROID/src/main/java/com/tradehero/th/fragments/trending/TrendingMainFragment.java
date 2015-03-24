package com.tradehero.th.fragments.trending;

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
import com.tradehero.th.R;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefDTOList;
import com.tradehero.th.api.games.ViralMiniGameDefListKey;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.games.ViralGamePopupDialogFragment;
import com.tradehero.th.fragments.position.StocksMainPositionListFragment;
import com.tradehero.th.persistence.games.ViralMiniGameDefListCache;
import com.tradehero.th.persistence.prefs.ShowViralGameDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import timber.log.Timber;

public class TrendingMainFragment extends DashboardFragment
{
    private static final String KEY_ASSET_CLASS = TrendingMainFragment.class.getName() + ".assetClass";
    private static final String KEY_EXCHANGE_ID = TrendingMainFragment.class.getName() + ".exchangeId";

    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;
    @Inject @ShowViralGameDialog TimingIntervalPreference showViralGameTimingIntervalPreference;
    @Inject Lazy<ViralMiniGameDefListCache> viralMiniGameDefListCache;
    @Inject CurrentUserId currentUserId;

    private static int lastType = 0;

    private TradingPagerAdapter tradingPagerAdapter;
    private Subscription viralSubscription;
    @Nullable private ExchangeIntegerId exchangeIdFromArguments;

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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exchangeIdFromArguments = getExchangeId(getArguments());
        getArguments().remove(KEY_EXCHANGE_ID);
        tradingPagerAdapter = new TradingPagerAdapter(this.getChildFragmentManager());
        AssetClass askedAssetClass = getAssetClass(getArguments());
        if (askedAssetClass != null)
        {
            try
            {
                lastType = TrendingTabType.getForAssetClass(askedAssetClass).ordinal();
            } catch (IllegalArgumentException e)
            {
                Timber.e(e, "Unhandled assetClass for user " + currentUserId.get());
            }
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.trending_main_fragment, container, false);
        ButterKnife.inject(this, view);
        initViews();
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int i, float v, int i2)
            {

            }

            @Override public void onPageSelected(int i)
            {
                TrendingStockFragment.setCancelFirstInit(true);
            }

            @Override public void onPageScrollStateChanged(int i)
            {

            }
        });
        return view;
    }

    private void initViews()
    {
        tabViewPager.setAdapter(tradingPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }

        pagerSlidingTabStrip.setCustomTabView(lastType == 0 ? R.layout.th_page_indicator : R.layout.th_tab_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);

        tabViewPager.setCurrentItem(0, true);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (showViralGameTimingIntervalPreference.isItTime())
        {
            viralSubscription = AppObservable.bindFragment(this, viralMiniGameDefListCache.get().get(new ViralMiniGameDefListKey()))
                    .take(1)
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
        setUpToolbarSpinner();
    }

    private void setUpToolbarSpinner() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int oldType = lastType;
                if (position == 0) {
                    lastType = 0;
                } else {
                    lastType = 1;
                }
                Timber.d("onItemSelected: " + parent.getItemAtPosition(position));
                if (oldType != lastType)
                {
                    initViews();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
                //do nothing
            }
        };
        configureDefaultSpinner(new String[] {
                        getString(R.string.stocks),
                        getString(R.string.fx)},
                listener, lastType);
    }

    @Override public void onPause()
    {
        unsubscribe(viralSubscription);
        hideToolbarSpinner();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        lastType = tabViewPager.getCurrentItem();
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.tradingPagerAdapter = null;
        super.onDestroy();
    }

    private class TradingPagerAdapter extends FragmentPagerAdapter
    {
        public TradingPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            Timber.d("lyl getItem position="+position+" lastType="+lastType);
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            Class fragmentClass = TrendingStockTabType.values()[position].fragmentClass;
            if (lastType == 1)
            {
                fragmentClass = TrendingFXTabType.values()[position].fragmentClass;
            }
            if (position == 0 && lastType == 1)
            {
                StocksMainPositionListFragment.putIsFX(args, AssetClass.FX);
            }
            args.putInt(TrendingStockFragment.KEY_TYPE_ID, position);
            DashboardFragment subFragment = (DashboardFragment) Fragment.instantiate(getActivity(), fragmentClass.getName(), args);
            return subFragment;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            if (lastType == 0)
            {
                return getString(TrendingStockTabType.values()[position].titleStringResId);
            }
            return getString(TrendingFXTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            if (lastType == 0)
            {
                return TrendingStockTabType.values().length;
            }
            return TrendingFXTabType.values().length;
        }

        @Override public long getItemId(int position)
        {
            return super.getItemId(position) + lastType*10;
        }
    }

}
