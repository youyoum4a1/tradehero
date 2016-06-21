package com.androidth.general.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.common.SlidingTabLayout;
import com.androidth.general.R;
import com.androidth.general.adapters.PagedDTOAdapter;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.BasicProviderSecurityListType;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.portfolio.AssetClass;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOUtil;
import com.androidth.general.api.security.key.SecurityListType;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.androidth.general.fragments.competition.CompetitionWebViewFragment;
import com.androidth.general.fragments.contestcenter.ContestCenterTabType;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.security.SecurityCompactListCacheRx;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.utils.DeviceUtil;


import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ProviderSecurityV2RxFragment extends BaseFragment
{
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject protected SecurityCompactListCacheRx securityCompactListCache;
    @Inject protected SecurityCompositeListCacheRx securityCompositeListCacheRx;

    @SuppressWarnings("UnusedDeclaration") @Inject
    Context doNotRemoveOrItFails;

    @Bind(R.id.android_tabs)
    SlidingTabLayout pagerSlidingTabLayout;
    @Bind(R.id.pager)
    ViewPager viewPager;


    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityV2RxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected TextView tradeTitleView;

    public static void putProviderId(@NonNull Bundle bundle, @NonNull ProviderId providerId)
    {
        bundle.putBundle(BUNDLE_PROVIDER_ID_KEY, providerId.getArgs());
    }

    @NonNull private static ProviderId getProviderId(@NonNull Bundle bundle)
    {
        Bundle providerBundle = bundle.getBundle(BUNDLE_PROVIDER_ID_KEY);
        if (providerBundle == null)
        {
            throw new NullPointerException("Provider needs to be passed");
        }
        return new ProviderId(providerBundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        //setActionBarTitle(this.providerDTO.name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contest_center, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProviderDTO();
        //requestDtos();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public void onDestroyView()
    {
        DeviceUtil.dismissKeyboard(getActivity());
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void initViews()
    {
        ProviderSecurityV2PagerAdapter adapter = new ProviderSecurityV2PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        pagerSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabLayout.setDistributeEvenly(true);
        pagerSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabLayout.setViewPager(viewPager);
    }

    @NonNull protected ArrayAdapter<SecurityCompactDTO> createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(getActivity(), R.layout.trending_security_item);
    }

    @NonNull public SecurityListType makePagedDtoKey(int page)
    {
        return new BasicProviderSecurityV2ListType(providerId);
    }

    protected void fetchProviderDTO()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                providerCache.get(this.providerId)
                        .map(new PairGetSecond<ProviderId, ProviderDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ProviderDTO>()
                        {
                            @Override public void call(ProviderDTO provider)
                            {
                                ProviderSecurityV2RxFragment.this.linkWith(provider);
                            }
                        },
                        new ToastOnErrorAction1(getString(R.string.error_fetch_provider_info))));
    }

    protected void linkWith(@NonNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        getActivity().invalidateOptionsMenu();
        getActivity().supportInvalidateOptionsMenu();
        displayTitle();
    }

    protected void displayTitle()
    {
        setActionBarTitle(providerDTO.name);
    }

    private class ProviderSecurityV2PagerAdapter extends FragmentPagerAdapter
    {
        public ProviderSecurityV2PagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            SecurityV2TabType tabType = SecurityV2TabType.values()[position];
            Bundle args = getArguments();
            if (args == null)
            {
                args = new Bundle();
            }
            return Fragment.instantiate(getActivity(), tabType.tabClass.getName(), args);
        }

        @Override public int getCount()
        {
            return SecurityV2TabType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(SecurityV2TabType.values()[position].titleRes);
        }
    }
}
