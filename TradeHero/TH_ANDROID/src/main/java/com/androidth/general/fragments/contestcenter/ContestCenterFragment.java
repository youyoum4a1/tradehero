package com.androidth.general.fragments.contestcenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.SlidingTabLayout;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.base.BaseLiveFragmentUtil;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.competition.CompetitionWebViewFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.persistence.competition.ProviderListCacheRx;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class ContestCenterFragment extends DashboardFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Bind(R.id.android_tabs) SlidingTabLayout pagerSlidingTabLayout;
    @Bind(R.id.pager) ViewPager viewPager;
    @Inject
    ProviderListCacheRx providerListCache;
    @Inject
    ProviderUtil providerUtil;
    ProviderDTOList providerDTOs;

    private BaseLiveFragmentUtil liveFragmentUtil;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_contest_center);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_contest_center, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        liveFragmentUtil.onResume();
        //loadContestData();
    }
    @Override public void onStart(){
        super.onStart();
        //loadContestData();
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
        liveFragmentUtil.setCallToAction(isLive);
    }

    @Override public void onDestroyView()
    {
        liveFragmentUtil.onDestroyView();
        liveFragmentUtil = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
    private void loadContestData()
    {
        // get the data
        fetchProviderIdList();
    }

    private void fetchProviderIdList()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, providerListCache.fetch(new ProviderListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ProviderDTOList>()
                        {
                            @Override public void call(ProviderDTOList latestProviderDTOs) {
                                providerDTOs = latestProviderDTOs;
                                if (providerDTOs != null && providerDTOs.size() != 0) {
                                    ProviderDTO providerDTO = providerDTOs.get(0);
                                    if (providerDTO != null && providerDTO.isUserEnrolled) {
                                        Bundle args = new Bundle();
                                        MainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
                                        OwnedPortfolioId applicablePortfolioId = providerDTO.getAssociatedOwnedPortfolioId();
                                        if (applicablePortfolioId != null) {
                                            MainCompetitionFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                                        }
                                        navigator.get().pushFragment(MainCompetitionFragment.class, args);
                                    } else if (providerDTO != null) {
                                        Bundle args = new Bundle();
                                        CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                                                providerDTO.getProviderId()
                                        ));
                                        navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
                                    }
                                }

                            }

                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                THToast.show(getString(R.string.error_fetch_provider_info_list));
                                Timber.e("Failed retrieving the list of competition providers", throwable);
                            }
                        }));
    }

    private void initViews()
    {
        ContestCenterPagerAdapter adapter = new ContestCenterPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        pagerSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabLayout.setDistributeEvenly(true);
        pagerSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabLayout.setViewPager(viewPager);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        liveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);
    }

    private class ContestCenterPagerAdapter extends FragmentPagerAdapter
    {
        public ContestCenterPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            ContestCenterTabType tabType = ContestCenterTabType.values()[position];
            Bundle args = getArguments();
            if (args == null)
            {
                args = new Bundle();
            }
            return Fragment.instantiate(getActivity(), tabType.tabClass.getName(), args);
        }

        @Override public int getCount()
        {
            return ContestCenterTabType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(ContestCenterTabType.values()[position].titleRes);
        }
    }
}
