package com.ayondo.academy.fragments.position;

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
import butterknife.Bind;
import com.android.common.SlidingTabLayout;
import com.ayondo.academy.R;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.base.ActionBarOwnerMixin;
import com.ayondo.academy.fragments.billing.BasePurchaseManagerFragment;
import com.ayondo.academy.utils.route.THRouter;
import javax.inject.Inject;

public class FXMainPositionListFragment extends BasePurchaseManagerFragment
{
    private static final String OWNED_PORTFOLIO_ID_BUNDLE_KEY = FXMainPositionListFragment.class.getName() + ".ownedPortfolioId";

    @Inject THRouter thRouter;
    @Inject CurrentUserId currentUserId;
    @Bind(R.id.pager) ViewPager tabViewPager;
    @Bind(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    protected OwnedPortfolioId fxPortfolioId;

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

    private static TabType[] FX_TYPES = new TabType[] {
            TabType.LONG,
    };

    public static void putMainFXPortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(OWNED_PORTFOLIO_ID_BUNDLE_KEY, ownedPortfolioId.getArgs());
    }

    @NonNull private static OwnedPortfolioId getMainFXPortfolioId(@NonNull Bundle args)
    {
        return new OwnedPortfolioId(args.getBundle(OWNED_PORTFOLIO_ID_BUNDLE_KEY));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        fxPortfolioId = getMainFXPortfolioId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tabbed_position_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tabViewPager.setAdapter(new TabbedPositionPageAdapter(getChildFragmentManager()));
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
        pagerSlidingTabStrip.setVisibility(View.GONE);
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
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
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            if (purchaseApplicableOwnedPortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, fxPortfolioId);
            PositionListFragment.putShownUser(args, currentUserId.toUserBaseKey());
            TabType positionType;
            positionType = FX_TYPES[position];
            PositionListFragment.putPositionType(args, positionType);
            args.putBoolean(PositionListFragment.BUNDLE_KEY_SHOW_TITLE, false);
            args.putBoolean(PositionListFragment.BUNDLE_KEY_IS_TRENDING_FX_PORTFOLIO, true);

            return Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            return FX_TYPES.length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(FX_TYPES[position].fxTitle);
        }
    }
}
