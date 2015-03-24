package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;

public class StocksMainPositionListFragment extends BasePurchaseManagerFragment
{
    //private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = StocksMainPositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_IS_FX = StocksMainPositionListFragment.class.getName() + "isFX";
    private static final String BUNDLE_KEY_PROVIDER_ID = StocksMainPositionListFragment.class + ".providerId";
    private static final boolean DEFAULT_IS_FX = false;

    @Inject THRouter thRouter;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    @NonNull protected GetPositionsDTOKey getPositionsDTOKey;
    protected PortfolioDTO portfolioDTO;
    @Nullable protected UserProfileDTO userProfileDTO;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

    boolean isFX;

    ProviderId providerId;

    public enum TabType
    {
        LONG(R.string.position_list_header_open_unsure, R.string.position_list_header_open_long_unsure),
        SHORT(R.string.position_list_header_open_short_unsure, R.string.position_list_header_open_short_unsure),
        CLOSED(R.string.position_list_header_closed_unsure, R.string.position_list_header_closed_unsure),;

        @StringRes private final int stockTitle;
        @StringRes private final int fxTitle;

        TabType(@StringRes int stockTitle, @StringRes int fxTitle)
        {
            this.stockTitle = stockTitle;
            this.fxTitle = fxTitle;
        }
    }

    private static TabType[] STOCK_TYPES = new TabType[] {
            TabType.LONG,
            TabType.CLOSED,
    };

    private static TabType[] FX_TYPES = new TabType[] {
            TabType.LONG,
            TabType.SHORT,
            TabType.CLOSED,
    };

    public static void putIsFX(@NonNull Bundle args, @Nullable AssetClass assetClass)
    {
        if (assetClass == null)
        {
            args.putBoolean(BUNDLE_KEY_IS_FX, DEFAULT_IS_FX);
        }
        args.putBoolean(BUNDLE_KEY_IS_FX, assetClass == AssetClass.FX);
    }

    private boolean isFX(@NonNull Bundle args)
    {
        return args.getBoolean(BUNDLE_KEY_IS_FX, DEFAULT_IS_FX);
    }

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    //@Nullable private ProviderId getProviderId(@NonNull Bundle args)
    //{
    //    Bundle bundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID);
    //    if (bundle == null)
    //    {
    //        return null;
    //    }
    //    return new ProviderId(bundle);
    //}

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        Bundle args = getArguments();
        isFX = isFX(args);
        if (isFX)
        {
            getPositionsDTOKey = new OwnedPortfolioId(currentUserId.get(), userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey()).fxPortfolio.id);
        }
        else
        {
            getPositionsDTOKey = new OwnedPortfolioId(currentUserId.get(), userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey()).portfolio.id);
        }
        //providerId = getProviderId(args);
    }


    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tabbed_position_fragment, container, false);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    private void initViews()
    {
        tabViewPager.setAdapter(new TabbedPositionPageAdapter(getChildFragmentManager()));
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle("");
    }

    private class TabbedPositionPageAdapter extends FragmentPagerAdapter
    {
        public TabbedPositionPageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            if (purchaseApplicableOwnedPortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, getPositionsDTOKey);
            PositionListFragment.putShownUser(args, currentUserId.toUserBaseKey());
            TabType positionType;
            if (isFX)
            {
                positionType = FX_TYPES[position];
            }
            else
            {
                positionType = STOCK_TYPES[position];
            }
            PositionListFragment.putPositionType(args, positionType);

            if (providerId != null)
            {
                CompetitionLeaderboardPositionListFragment.putProviderId(args, providerId);
                return Fragment.instantiate(getActivity(), CompetitionLeaderboardPositionListFragment.class.getName(), args);
            }
            else if (getPositionsDTOKey instanceof LeaderboardMarkUserId)
            {
                return Fragment.instantiate(getActivity(), LeaderboardPositionListFragment.class.getName(), args);
            }
            return Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            if (isFX)
            {
                return FX_TYPES.length;
            }
            else
            {
                return STOCK_TYPES.length;
            }
        }

        @Override public CharSequence getPageTitle(int position)
        {
            if (isFX)
            {
                return getString(FX_TYPES[position].fxTitle);
            }
            else
            {
                return getString(STOCK_TYPES[position].stockTitle);
            }
        }
    }
}
