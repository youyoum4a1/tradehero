package com.androidth.general.fragments.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.SlidingTabLayout;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.security.ProviderSortCategoryDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.api.security.key.SecurityListType;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.security.SecurityCompactListCacheRx;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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


    public static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityV2RxFragment.class.getName() + ".providerId";
    public static final String BUNDLE_SORT_AVAILABILE_KEY = ProviderSecurityV2RxFragment.class.getName() + ".sortAvailableFlag";
    public static final String BUNDLE_SECURITIES_KEY = ProviderSecurityV2RxFragment.class.getName() + ".securitiesKey";

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    protected TextView tradeTitleView;

    private boolean isSortAvailable = false;

    protected SecurityCompositeDTO securityCompositeDTO;

    private ArrayList<SecurityCompactDTO> securityCompactDTOList;
    private List<ProviderSortCategoryDTO> providerSortCategoryDTOList;

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

    public static void setSortAvailableFlag(@NonNull Bundle bundle, @NonNull boolean isSortAvailable)
    {
        bundle.putBoolean(BUNDLE_SORT_AVAILABILE_KEY, isSortAvailable);
    }

    @NonNull private static boolean getSortAvailableFlag(@NonNull Bundle bundle)
    {
        return bundle.getBoolean(BUNDLE_SORT_AVAILABILE_KEY, false);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
        this.isSortAvailable = getSortAvailableFlag(getArguments());

        securityCompositeDTO = securityCompositeListCacheRx.getCachedValue(new BasicProviderSecurityV2ListType(providerId));

        this.securityCompactDTOList = securityCompositeDTO.Securities;
        this.providerSortCategoryDTOList = securityCompositeDTO.SortCategories;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        //setActionBarTitle(this.providerDTO.name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contest_securities, container, false);
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
        //displayTitle();
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
        ProviderSecurityV2PagerAdapter adapter = new ProviderSecurityV2PagerAdapter(getChildFragmentManager(), isSortAvailable);
        viewPager.setAdapter(adapter);
        pagerSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabLayout.setDistributeEvenly(true);
        pagerSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.general_tab_indicator_color));
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
        //setActionBarTitle(providerDTO.name);
        if(providerDTO != null) {
            setActionBarTitle("");
            setActionBarColor(providerDTO.hexColor);
            setActionBarImage(providerDTO.navigationLogoUrl);
        }
    }

    private void setActionBarImage(String url){
        setActionBarCustomImage(getActivity(), url, false);
    }

    private class ProviderSecurityV2PagerAdapter extends FragmentPagerAdapter
    {
        private boolean isSortAvailable = false;
        public ProviderSecurityV2PagerAdapter(FragmentManager fm, boolean isSortAvailable)
        {
            super(fm);
            this.isSortAvailable = isSortAvailable;
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = getArguments();
            if (args == null)
            {
                args = new Bundle();
            }

            String className = "";
            if(isSortAvailable){
                Bundle args1 = new Bundle();//need to have new arguments, otherwise, it keeps adding up
                args1.putAll(getArguments());

                if(securityCompactDTOList!=null){
                    String sortKey = providerSortCategoryDTOList.get(position).getSortOrder();
                    String sortDirection = providerSortCategoryDTOList.get(position).getSortDirection();

                    SecurityComparator securityComparator = new SecurityComparator();
                    securityComparator.setSortKey(sortKey);
                    securityComparator.setSortDirection(sortDirection);

                    ArrayList<SecurityCompactDTO> list = new ArrayList<>();//must have new list, otherwise, sort

                    list.addAll(securityCompactDTOList);
                    Collections.sort(list, securityComparator);

                    //the reason for having a new instance of args
                    args1.putParcelableArrayList(ProviderSecurityV2RxFragment.BUNDLE_SECURITIES_KEY, list);

                }

                return Fragment.instantiate(getActivity(), ProviderSecurityV2RxSubFragment.class.getName(), args1);

            }else{
                SecurityV2TabType tabType = SecurityV2TabType.values()[position];
                className = tabType.tabClass.getName();
                return Fragment.instantiate(getActivity(), className, args);
            }
        }

        @Override public int getCount()
        {
            if(isSortAvailable){
                return providerSortCategoryDTOList.size();
            }else{
                return SecurityV2TabType.values().length;
            }
        }

        @Override public CharSequence getPageTitle(int position)
        {
            if(isSortAvailable){
                return providerSortCategoryDTOList.get(position).getName();
            }else{
                return getString(SecurityV2TabType.values()[position].titleRes);
            }
        }
    }

    public class SecurityComparator implements Comparator<SecurityCompactDTO> {

        String sortKey = "";
        String sortDirection = "";

        public void setSortKey(String sortKey){
            this.sortKey = sortKey;
        }

        public void setSortDirection(String sortDirection){
            this.sortDirection = sortDirection;
        }

        @Override
        public int compare(SecurityCompactDTO lhs, SecurityCompactDTO rhs) {

            switch (sortKey) {
                case "volume":
                    Double left, right;
                    if (lhs.getVolume() != null) {
                        left = lhs.getVolume();
                    } else {
                        left = 0.00;
                    }
                    if (rhs.getVolume() != null) {
                        right = rhs.getVolume();
                    } else {
                        right = 0.00;
                    }
                    return right.compareTo(left);

                case "risePercent":
                    if(sortDirection.equals("DESC")){
                        return rhs.getRisePercent().compareTo(lhs.getRisePercent());
                    }else{
                        return lhs.getRisePercent().compareTo(rhs.getRisePercent());
                    }
                default:
                    break;
            }
            return lhs.name.compareTo(rhs.name);//default
        }
    }

//    public static void setItems(List<SecurityCompactDTO> items)
//    {
//
//        ProviderSecurityV2RxFragment.items = items;
//
//    }
}
