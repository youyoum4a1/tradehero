package com.tradehero.th.fragments.security;

import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentSecurityComponent
{
    void injectSecuritySearchFragment(SecuritySearchFragment target);
    void injectSecuritySearchWatchlistFragment(SecuritySearchWatchlistFragment target);
    void injectSecuritySearchProviderFragment(SecuritySearchProviderFragment target);
    void injectSecurityItemViewAdapter(SecurityItemViewAdapter target);
    void injectSecurityItemView(SecurityItemView target);
    void injectWarrantSecurityItemView(WarrantSecurityItemView target);
    void injectChartFragment(ChartFragment target);
    void injectStockInfoValueFragment(StockInfoValueFragment target);
    void injectWarrantInfoValueFragment(WarrantInfoValueFragment target);
    void injectStockInfoFragment(StockInfoFragment target);
    void injectWatchlistEditFragment(WatchlistEditFragment target);
    void injectSecurityActionListLinear(SecurityActionListLinear target);
}
