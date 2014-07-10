package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.news.NewsHeadlineAdapter;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.viewpagerindicator.PageIndicator;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class StockInfoFragment extends DashboardFragment
{
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = StockInfoFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = StockInfoFragment.class.getName() + ".providerId";

    @Inject protected AlertDialogUtil alertDialogUtil;

    protected ProviderId providerId;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> compactCacheListener;

    protected PaginatedDTO<NewsItemDTO> newsHeadlineList;
    private DTOCache.Listener<SecurityId, PaginatedDTO<NewsItemDTO>> yahooNewsCacheListener;
    private DTOCache.GetOrFetchTask<SecurityId, PaginatedDTO<NewsItemDTO>> yahooNewsCacheFetchTask;

    private MenuItem marketCloseIcon;

    private ViewPager topPager;
    private InfoTopStockPagerAdapter topViewPagerAdapter;
    private PageIndicator topPagerIndicator;
    private NewsHeadlineAdapter newsHeadlineAdapter;
    private ListView yahooNewsListView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        compactCacheListener = createSecurityCompactCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_info, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        newsHeadlineAdapter = new NewsHeadlineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.news_headline_item_view);

        yahooNewsListView = (ListView) view.findViewById(R.id.list_news_headline);
        if (yahooNewsListView != null)
        {
            yahooNewsListView.setAdapter(newsHeadlineAdapter);
            yahooNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handleNewsClicked(position, (NewsItemDTOKey) adapterView.getItemAtPosition(position));
                }
            });
        }

        topPager = (ViewPager) view.findViewById(R.id.top_pager);
        if (topViewPagerAdapter == null)
        {
            topViewPagerAdapter = new InfoTopStockPagerAdapter(getActivity(), ((Fragment) this).getChildFragmentManager());
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
            Bundle providerIdBundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID_BUNDLE);
            if (providerIdBundle != null)
            {
                linkWith(new ProviderId(providerIdBundle), false);
            }
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            if (securityIdBundle != null)
            {
                linkWith(new SecurityId(securityIdBundle), false);
            }
            display();
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.stock_info_menu, menu);
        displayExchangeSymbol();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        marketCloseIcon = menu.findItem(R.id.buy_sell_menu_market_status);

        displayMarketClose();
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
    }

    @Override public void onPause()
    {
        detachSecurityCompactCache();

        if (yahooNewsCacheFetchTask != null)
        {
            yahooNewsCacheFetchTask.setListener(null);
            yahooNewsCacheFetchTask.cancel(false);
        }
        yahooNewsCacheFetchTask = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (yahooNewsListView != null)
        {
            yahooNewsListView.setOnItemClickListener(null);
        }
        yahooNewsListView = null;
        newsHeadlineAdapter = null;
        topViewPagerAdapter = null;
        topPager = null;
        topPagerIndicator = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        compactCacheListener = null;
        super.onDestroy();
    }

    protected void detachSecurityCompactCache()
    {
        securityCompactCache.get().unregister(compactCacheListener);
    }

    private void linkWith(final ProviderId providerId, final boolean andDisplay)
    {
        this.providerId = providerId;

        if (andDisplay)
        {
            // TODO
        }
    }

    private void linkWith(final SecurityId securityId, final boolean andDisplay)
    {
        this.securityId = securityId;

        if (securityId != null)
        {
            queryCompactCache(securityId, andDisplay);
            //queryNewsCache(securityId, andDisplay);
        }

        if (andDisplay)
        {
            displayExchangeSymbol();
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
            detachSecurityCompactCache();
            securityCompactCache.get().register(securityId, compactCacheListener);
            securityCompactCache.get().getOrFetchAsync(securityId);
        }
    }

    //private void queryNewsCache(final SecurityId securityId, final boolean andDisplay)
    //{
    //    PaginatedDTO<NewsItemDTO> newsHeadlineList = newsCache.get().get(securityId);
    //    if (newsHeadlineList != null)
    //    {
    //        linkWith(newsHeadlineList, andDisplay);
    //    }
    //    else
    //    {
    //        yahooNewsCacheListener = new DTOCache.Listener<SecurityId, PaginatedDTO<NewsItemDTO>>()
    //        {
    //            @Override public void onDTOReceived(SecurityId key, PaginatedDTO<NewsItemDTO> value, boolean fromCache)
    //            {
    //                linkWith(value, andDisplay);
    //            }
    //
    //            @Override public void onErrorThrown(SecurityId key, Throwable error)
    //            {
    //                THToast.show(R.string.error_fetch_news_list);
    //                Timber.e(error, "Failed to fetch NewsHeadlineList for %s", securityId, error);
    //            }
    //        };
    //
    //        if (yahooNewsCacheFetchTask != null)
    //        {
    //            yahooNewsCacheFetchTask.cancel(true);
    //        }
    //        yahooNewsCacheFetchTask = newsCache.get().getOrFetch(securityId, yahooNewsCacheListener);
    //        yahooNewsCacheFetchTask.execute();
    //    }
    //}

    private void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;

        if (andDisplay)
        {
            displayMarketClose();
            displayTopViewPager();
        }
    }

    private void linkWith(PaginatedDTO<NewsItemDTO> newsHeadlineList, boolean andDisplay)
    {
        this.newsHeadlineList = newsHeadlineList;

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
        if (securityId != null)
        {
            setActionBarTitle(String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol()));
        }
        else
        {
            setActionBarTitle("-:-");
        }
    }

    private void displayMarketClose()
    {
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setVisible(securityCompactDTO != null && !securityCompactDTO.marketOpen);
        }
    }

    public void displayTopViewPager()
    {
        if (topViewPagerAdapter != null)
        {
            SecurityCompactDTO adapterSecurityDTO = topViewPagerAdapter.getSecurityCompactDTO();
            if (securityId != null && (adapterSecurityDTO == null || !securityId.equals(adapterSecurityDTO.getSecurityId())))
            {
                topViewPagerAdapter.linkWith(providerId);
                topViewPagerAdapter.linkWith(securityCompactDTO);
                topViewPagerAdapter.notifyDataSetChanged();
            }
        }
    }

    private void displayYahooNewsList()
    {
        if (newsHeadlineAdapter != null && newsHeadlineList !=null)
        {
            List<NewsItemDTO> data = newsHeadlineList.getData();
            List<NewsItemDTOKey> newsItemDTOKeyList = new ArrayList<>();

            if (data != null)
            {
                for (NewsItemDTO newsItemDTO: data)
                {
                    newsItemDTOKeyList.add(newsItemDTO.getDiscussionKey());
                }
            }

            newsHeadlineAdapter.setSecurityId(securityId);
            newsHeadlineAdapter.setItems(newsItemDTOKeyList);
            newsHeadlineAdapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(int position, NewsItemDTOKey newsItemDTOKey)
    {
        Navigator navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
        Bundle bundle = new Bundle();
        NewsDiscussionFragment.putDiscussionKey(bundle, newsItemDTOKey);
        int resId = newsHeadlineAdapter.getBackgroundRes(position);
        NewsDiscussionFragment.putBackgroundResId(bundle, resId);
        navigator.pushFragment(NewsDiscussionFragment.class, bundle);
    }

    protected DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> createSecurityCompactCacheListener()
    {
        return new StockInfoFragmentSecurityCompactCacheListener();
    }

    protected class StockInfoFragmentSecurityCompactCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityCompactDTO>
    {
        @Override public void onDTOReceived(@NotNull SecurityId key, @NotNull SecurityCompactDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_security_info);
            Timber.e(error, "Failed to fetch SecurityCompact %s", securityId);
        }
    }
}
