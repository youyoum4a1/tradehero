package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.NewsList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.yahoo.NewsCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.viewpagerindicator.PageIndicator;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/31/13 Time: 10:46 AM To change this template use File | Settings | File Templates. */
public class StockInfoFragment extends DashboardFragment
{
    public static final String TAG = StockInfoFragment.class.getSimpleName();
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = StockInfoFragment.class.getName() + ".securityId";

    protected SecurityId securityId;

    protected SecurityCompactDTO securityCompactDTO;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCache.Listener<SecurityId, SecurityCompactDTO> compactCacheListener;
    private DTOCache.GetOrFetchTask<SecurityCompactDTO> compactCacheFetchTask;

    protected NewsList yahooNewsList;
    @Inject Lazy<NewsCache> yahooNewsCache;
    private DTOCache.Listener<SecurityId, NewsList> yahooNewsCacheListener;
    private DTOCache.GetOrFetchTask<NewsList> yahooNewsCacheFetchTask;

    private ActionBar actionBar;
    private ImageView marketCloseIcon;

    private ViewPager topPager;
    private InfoTopStockPagerAdapter topViewPagerAdapter;
    private PageIndicator topPagerIndicator;
    private YahooNewsListView yahooNewsListView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_info, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        yahooNewsListView = (YahooNewsListView) view.findViewById(R.id.list_yahooNews);
        if (yahooNewsListView != null)
        {
            yahooNewsListView.setAdapter(getActivity(), getActivity().getLayoutInflater());
        }

        topPager = (ViewPager) view.findViewById(R.id.top_pager);
        if (topViewPagerAdapter == null)
        {
            topViewPagerAdapter = new InfoTopStockPagerAdapter(getActivity(), getFragmentManager());
        }
        if (topPager != null)
        {
            topPager.setAdapter(topViewPagerAdapter);
        }

        topPagerIndicator = (PageIndicator) view.findViewById(R.id.top_pager_indicator);
        if (topPagerIndicator != null && topPager != null)
        {
            topPagerIndicator.setViewPager(topPager, 0);
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            if (securityIdBundle != null)
            {
                linkWith(new SecurityId(securityIdBundle), true);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.stock_info_menu, menu);
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        displayExchangeSymbol();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuElements = menu.findItem(R.id.menu_elements_stock_info);

        marketCloseIcon = (ImageView) menuElements.getActionView().findViewById(R.id.market_status);
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    handleMarketCloseClicked();
                }
            });
        }

        displayMarketClose();
    }

    @Override public void onDestroyOptionsMenu()
    {
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setOnClickListener(null);
        }
        marketCloseIcon = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onPause()
    {
        if (compactCacheFetchTask != null)
        {
            compactCacheFetchTask.forgetListener(true);
            compactCacheFetchTask.cancel(false);
        }
        compactCacheFetchTask = null;

        if (yahooNewsCacheFetchTask != null)
        {
            yahooNewsCacheFetchTask.forgetListener(true);
            yahooNewsCacheFetchTask.cancel(false);
        }
        yahooNewsCacheFetchTask = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (yahooNewsListView != null)
        {
            yahooNewsListView.onDestroyView();
        }
        topViewPagerAdapter = null;
        topPager = null;
        topPagerIndicator = null;
        super.onDestroyView();
    }

    private void linkWith(final SecurityId securityId, final boolean andDisplay)
    {
        this.securityId = securityId;

        if (securityId != null)
        {
            queryCompactCache(securityId, andDisplay);
            queryNewsCache(securityId, andDisplay);
        }

        if (andDisplay)
        {
            displayExchangeSymbol();
            displayTopViewPager();
        }
    }

    private void queryCompactCache(final SecurityId securityId, final boolean andDisplay)
    {
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityId);
        if (securityCompactDTO != null)
        {
            linkWith(securityCompactDTO, andDisplay);
        }
        else
        {
            compactCacheListener = new DTOCache.Listener<SecurityId, SecurityCompactDTO>()
            {
                @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value)
                {
                    linkWith(value, andDisplay);
                }

                @Override public void onErrorThrown(SecurityId key, Throwable error)
                {
                    THToast.show(R.string.error_fetch_security_info);
                    THLog.e(TAG, "Failed to fetch SecurityCompact for " + securityId, error);
                }
            };

            if (compactCacheFetchTask != null)
            {
                compactCacheFetchTask.cancel(true);
            }
            compactCacheFetchTask = securityCompactCache.get().getOrFetch(securityId, compactCacheListener);
            compactCacheFetchTask.execute();
        }
    }

    private void queryNewsCache(final SecurityId securityId, final boolean andDisplay)
    {
        NewsList newsList = yahooNewsCache.get().get(securityId);
        if (newsList != null)
        {
            linkWith(newsList, andDisplay);
        }
        else
        {
            yahooNewsCacheListener = new DTOCache.Listener<SecurityId, NewsList>()
            {
                @Override public void onDTOReceived(SecurityId key, NewsList value)
                {
                    linkWith(value, andDisplay);
                }

                @Override public void onErrorThrown(SecurityId key, Throwable error)
                {
                    THToast.show(R.string.error_fetch_news_list);
                    THLog.e(TAG, "Failed to fetch NewsList for " + securityId, error);
                }
            };

            if (yahooNewsCacheFetchTask != null)
            {
                yahooNewsCacheFetchTask.cancel(true);
            }
            yahooNewsCacheFetchTask = yahooNewsCache.get().getOrFetch(securityId, yahooNewsCacheListener);
            yahooNewsCacheFetchTask.execute();
        }
    }

    private void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;

        if (andDisplay)
        {
            displayMarketClose();
        }
    }

    private void linkWith(NewsList newsList, boolean andDisplay)
    {
        this.yahooNewsList = newsList;

        if (andDisplay)
        {
            displayYahooNewsList();
        }
    }

    private void display()
    {
        displayExchangeSymbol();
        displayMarketClose();
        displayTopViewPager();
        displayYahooNewsList();
    }

    private void displayExchangeSymbol()
    {
        if (actionBar != null)
        {
            if (securityId != null)
            {
                actionBar.setTitle(String.format("%s:%s", securityId.exchange, securityId.securitySymbol));
            }
            else
            {
                actionBar.setTitle("-:-");
            }
        }
    }

    private void displayMarketClose()
    {
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setVisibility(securityCompactDTO == null || securityCompactDTO.marketOpen ? View.GONE : View.VISIBLE);
        }
    }

    public void displayTopViewPager()
    {
        if (topViewPagerAdapter != null)
        {
            if (securityId != null && !securityId.equals(topViewPagerAdapter.getSecurityId()))
            {
                topViewPagerAdapter.linkWith(securityId);

                if (topPager != null)
                {
                    topPager.post(new Runnable()
                    {
                        @Override public void run()
                        {
                            // We need to do it in a later frame otherwise the pager adapter crashes with IllegalStateException
                            topViewPagerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

    private void displayYahooNewsList()
    {
        if (yahooNewsListView != null)
        {
            yahooNewsListView.display(yahooNewsList);
        }
    }

    protected void handleMarketCloseClicked()
    {
        if (securityId == null)
        {
            AlertDialogUtil.popWithNegativeButton(getActivity(),
                    R.string.alert_dialog_market_close_title,
                    R.string.alert_dialog_market_close_message_basic,
                    R.string.alert_dialog_market_close_cancel);
        }
        else
        {
            AlertDialogUtil.popWithNegativeButton(getActivity(),
                    getString(R.string.alert_dialog_market_close_title),
                    String.format(getString(R.string.alert_dialog_market_close_message), securityId.exchange, securityId.securitySymbol),
                    getString(R.string.alert_dialog_market_close_cancel));
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
