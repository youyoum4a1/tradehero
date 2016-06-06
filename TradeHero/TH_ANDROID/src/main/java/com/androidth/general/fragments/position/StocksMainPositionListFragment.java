package com.androidth.general.fragments.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import butterknife.ButterKnife;
import butterknife.Bind;
import com.android.common.SlidingTabLayout;
import com.androidth.general.R;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class StocksMainPositionListFragment extends BasePurchaseManagerFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;

    @Bind(R.id.pager) ViewPager tabViewPager;
    @Bind(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    protected Subscription portfolioIdSubscription;

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
    };

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tabbed_position_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        portfolioIdSubscription = AppObservable.bindSupportFragment(
                this,
                portfolioCompactListCache.getOne(currentUserId.toUserBaseKey()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, Observable<OwnedPortfolioId>>()
                        {
                            @Override public Observable<OwnedPortfolioId> call(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                            {
                                PortfolioCompactDTO defaultPortfolio = pair.second.getDefaultPortfolio();
                                if (defaultPortfolio == null)
                                {
                                    return Observable.error(new NullPointerException("Default portfolio is null"));
                                }
                                return Observable.just(defaultPortfolio.getOwnedPortfolioId());
                            }
                        })
                        .subscribe(
                                new Action1<OwnedPortfolioId>()
                                {
                                    @Override public void call(OwnedPortfolioId ownedPortfolioId)
                                    {
                                        tabViewPager.setAdapter(new TabbedPositionPageAdapter(getChildFragmentManager(), ownedPortfolioId));
                                        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
                                        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
                                        pagerSlidingTabStrip.setViewPager(tabViewPager);
                                        pagerSlidingTabStrip.setVisibility(View.GONE);
                                    }
                                },
                                new ToastOnErrorAction1());
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyView()
    {
        portfolioIdSubscription.unsubscribe();
        super.onDestroyView();
    }

    private class TabbedPositionPageAdapter extends FragmentPagerAdapter
    {
        @NonNull private final OwnedPortfolioId portfolioId;

        public TabbedPositionPageAdapter(FragmentManager fm, @NonNull OwnedPortfolioId portfolioId)
        {
            super(fm);
            this.portfolioId = portfolioId;
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            if (purchaseApplicableOwnedPortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, portfolioId);
            PositionListFragment.putShownUser(args, currentUserId.toUserBaseKey());
            TabType positionType;
            positionType = STOCK_TYPES[position];
            PositionListFragment.putPositionType(args, positionType);
            args.putBoolean(PositionListFragment.BUNDLE_KEY_SHOW_TITLE, false);
            return Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            return STOCK_TYPES.length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(STOCK_TYPES[position].stockTitle);
        }
    }
}
