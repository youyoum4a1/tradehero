package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.news.NewsHeadlineAdapter;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class StockInfoFragment extends DashboardFragment
{
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = StockInfoFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = StockInfoFragment.class.getName() + ".providerId";

    @Inject protected AlertDialogUtil alertDialogUtil;

    protected ProviderId providerId;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject Lazy<SecurityCompactCacheRx> securityCompactCache;

    protected PaginatedDTO<NewsItemDTO> newsHeadlineList;

    private MenuItem marketCloseIcon;

    private ViewPager topPager;
    private InfoTopStockPagerAdapter topViewPagerAdapter;
    private NewsHeadlineAdapter newsHeadlineAdapter;
    private ListView yahooNewsListView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_info, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        newsHeadlineAdapter = new NewsHeadlineAdapter(getActivity(), R.layout.news_headline_item_view);

        yahooNewsListView = (ListView) view.findViewById(R.id.list_news_headline);
        if (yahooNewsListView != null)
        {
            yahooNewsListView.setAdapter(newsHeadlineAdapter);
            yahooNewsListView.setOnItemClickListener(
                    (adapterView, view1, position, l) -> handleNewsClicked(position, (NewsItemDTOKey) adapterView.getItemAtPosition(position)));
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
        super.onDestroyView();
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
        AndroidObservable.bindFragment(this, securityCompactCache.get().get(securityId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSecurityCompactCacheObserver());
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
        SecurityCompactDTO securityCompactDTOCopy = securityCompactDTO;
        MenuItem marketCloseIconCopy = marketCloseIcon;
        if (marketCloseIconCopy != null)
        {
            marketCloseIconCopy.setVisible(securityCompactDTOCopy != null
                    && securityCompactDTOCopy.marketOpen != null
                    && !securityCompactDTOCopy.marketOpen);
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
        Bundle bundle = new Bundle();
        NewsDiscussionFragment.putDiscussionKey(bundle, newsItemDTOKey);
        int resId = newsHeadlineAdapter.getBackgroundRes(position);
        NewsDiscussionFragment.putBackgroundResId(bundle, resId);
        navigator.get().pushFragment(NewsDiscussionFragment.class, bundle);
    }

    protected Observer<Pair<SecurityId, SecurityCompactDTO>> createSecurityCompactCacheObserver()
    {
        return new StockInfoFragmentSecurityCompactCacheObserver();
    }

    protected class StockInfoFragmentSecurityCompactCacheObserver implements Observer<Pair<SecurityId, SecurityCompactDTO>>
    {
        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_security_info);
            Timber.e(e, "Failed to fetch SecurityCompact");
        }
    }
}
