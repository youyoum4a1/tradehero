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
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;

public class FXMainPositionListFragment extends BasePurchaseManagerFragment
{
    @Inject THRouter thRouter;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    @NonNull protected GetPositionsDTOKey getPositionsDTOKey;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        getPositionsDTOKey = new OwnedPortfolioId(currentUserId.get(), userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey()).fxPortfolio.id);
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
        pagerSlidingTabStrip.setVisibility(View.GONE);
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
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            if (purchaseApplicableOwnedPortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, getPositionsDTOKey);
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
