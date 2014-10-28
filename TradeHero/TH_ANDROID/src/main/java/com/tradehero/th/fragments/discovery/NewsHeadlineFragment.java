package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.news.key.NewsItemListFeaturedKey;
import com.tradehero.th.api.news.key.NewsItemListGlobalKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListKeyHelper;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.rx.PaginationObservable;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class NewsHeadlineFragment extends Fragment
{
    private static final String NEWS_TYPE_KEY = NewsHeadlineFragment.class.getName() + ".newsType";

    @InjectView(R.id.content_wrapper) BetterViewAnimator mContentWrapper;
    @InjectView(R.id.discovery_news_list) PullToRefreshListView mNewsListView;

    private Subscription newsSubscription;
    private PaginationInfoDTO lastPaginationInfoDTO;
    private ProgressBar mBottomLoadingView;

    @OnItemClick(android.R.id.list) void handleNewsItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        NewsItemDTOKey newsItemDTOKey = (NewsItemDTOKey) parent.getItemAtPosition(position);
        NewsItemCompactDTO newsItemDTO = (NewsItemCompactDTO) discussionCache.get(newsItemDTOKey);

        if (newsItemDTO != null && newsItemDTO.url != null)
        {
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, newsItemDTO.url);
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
    }

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject DiscussionCache discussionCache;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

    private int mDisplayedViewId;
    private NewsHeadlineAdapter mFeaturedNewsAdapter;
    protected NewsItemListKey newsItemListKey;
    private AbsListView.OnScrollListener scrollListener;

    public NewsHeadlineFragment()
    {
        super();
    }

    private NewsItemListKey newsItemListKeyFromNewsType(NewsType newsType)
    {
        switch (newsType)
        {
            case MotleyFool:
                return new NewsItemListFeaturedKey(null, null);
            case Global:
                return new NewsItemListGlobalKey(null, null);
            default:
                return null;
        }
    }

    public static NewsHeadlineFragment newInstance(NewsType newsType)
    {
        if (newsType == NewsType.Region)
        {
            return new RegionalNewsHeadlineFragment();
        }

        NewsHeadlineFragment newsHeadlineFragment = new NewsHeadlineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NEWS_TYPE_KEY, newsType.ordinal());
        newsHeadlineFragment.setArguments(bundle);
        return newsHeadlineFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null)
        {
            int newsTypeOrdinal = args.getInt(NEWS_TYPE_KEY);
            if (newsTypeOrdinal >= 0 && newsTypeOrdinal < NewsType.values().length)
            {
                newsItemListKey = newsItemListKeyFromNewsType(NewsType.values()[newsTypeOrdinal]);
            }
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_featured_news, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mFeaturedNewsAdapter = new NewsHeadlineAdapter(getActivity(), R.layout.news_headline_item_view);

        View paddingHeader = new View(getActivity());
        paddingHeader.setLayoutParams(new AbsListView.LayoutParams(1, getResources().getDimensionPixelOffset(R.dimen.discovery_news_carousel_height)));
        mNewsListView.getRefreshableView().addHeaderView(paddingHeader);
        mBottomLoadingView = new ProgressBar(getActivity());
        mNewsListView.getRefreshableView().addFooterView(mBottomLoadingView);
        mNewsListView.setOnScrollListener(new MultiScrollListener(scrollListener, dashboardBottomTabsScrollListener));
        mNewsListView.setAdapter(mFeaturedNewsAdapter);

        Observable<NewsItemListKey> newsRangeObservable = Observable
                .create((Observable.OnSubscribe<PullToRefreshBase.Mode>) subscriber -> {
                    mNewsListView.setOnRefreshListener(
                            listViewPullToRefreshBase -> subscriber.onNext(listViewPullToRefreshBase.getCurrentMode()));

                    mNewsListView.setOnLastItemVisibleListener(() -> {
                        if (!mNewsListView.isRefreshing() && !mFeaturedNewsAdapter.isEmpty())
                        {
                            mBottomLoadingView.setVisibility(View.VISIBLE);
                            subscriber.onNext(PullToRefreshBase.Mode.PULL_FROM_END);
                        }
                    });
                })
                .map(mode -> {
                    switch (mode)
                    {
                        case PULL_FROM_END:
                            return NewsItemListKeyHelper.copy(newsItemListKey, lastPaginationInfoDTO.next);
                        case PULL_FROM_START:
                        default:
                            return NewsItemListKeyHelper.copy(newsItemListKey, new PaginationDTO(1, newsItemListKey.perPage));
                    }
                })
                .startWith(newsItemListKey);

        PublishSubject<List<NewsItemDTOKey>> newsSubject = PublishSubject.create();
        newsSubject.subscribe(mFeaturedNewsAdapter::setItems);
        newsSubject.subscribe(new UpdateUIObserver());

        newsSubscription = rxLoaderManager.create(newsItemListKey,
                PaginationObservable.createFromRange(newsRangeObservable, (Func1<NewsItemListKey, Observable<List<NewsItemDTOKey>>>)
                        key -> newsServiceWrapper.getNewsRx(key)
                                .doOnNext(newsItemCompactDTOPaginatedDTO -> lastPaginationInfoDTO = newsItemCompactDTOPaginatedDTO.getPagination())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(PaginatedDTO::getData)
                                .flatMap(Observable::from)
                                .map(NewsItemCompactDTO::getDiscussionKey)
                                .toList()))
                .subscribe(newsSubject);
    }

    public void setScrollListener(AbsListView.OnScrollListener scrollListener)
    {
        this.scrollListener = scrollListener;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (mDisplayedViewId > 0)
        {
            mContentWrapper.setDisplayedChildByLayoutId(mDisplayedViewId);
        }
    }

    @Override public void onDestroyView()
    {
        newsSubscription.unsubscribe();
        rxLoaderManager.remove(newsItemListKey);
        super.onDestroyView();
    }

    @Override public void onPause()
    {
        mDisplayedViewId = mContentWrapper.getDisplayedChildLayoutId();
        super.onPause();
    }

    private class UpdateUIObserver implements rx.Observer<List<NewsItemDTOKey>>
    {
        private void updateUI()
        {
            mContentWrapper.setDisplayedChildByLayoutId(mNewsListView.getId());
            mBottomLoadingView.setVisibility(View.INVISIBLE);
            mNewsListView.onRefreshComplete();
        }

        @Override public void onCompleted()
        {
            updateUI();
        }

        @Override public void onError(Throwable e)
        {
            updateUI();
        }

        @Override public void onNext(List<NewsItemDTOKey> newsItemDTOKeys)
        {
            updateUI();
        }
    }
}
