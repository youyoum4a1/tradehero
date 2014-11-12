package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
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
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.rx.PaginationObservable;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class NewsHeadlineFragment extends Fragment
{
    private static final String NEWS_TYPE_KEY = NewsHeadlineFragment.class.getName() + ".newsType";

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.discovery_news_list) ListView mNewsListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private Subscription newsSubscription;
    private PaginationInfoDTO lastPaginationInfoDTO;
    private ProgressBar mBottomLoadingView;
    private PublishSubject<List<NewsItemDTOKey>> newsSubject;
    private Observable<List<NewsItemDTOKey>> paginatedNewsItemListKeyObservable;

    @OnItemClick(R.id.discovery_news_list) void handleNewsItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        NewsItemDTOKey newsItemDTOKey = (NewsItemDTOKey) parent.getItemAtPosition(position);
        NewsItemCompactDTO newsItemDTO = (NewsItemCompactDTO) discussionCache.getValue(newsItemDTOKey);

        if (newsItemDTO != null && newsItemDTO.url != null)
        {
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, newsItemDTO.url);
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
    }

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject DiscussionCacheRx discussionCache;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

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

        mBottomLoadingView = new ProgressBar(getActivity());
        mBottomLoadingView.setVisibility(View.GONE);
        mNewsListView.addFooterView(mBottomLoadingView);
        mNewsListView.setAdapter(mFeaturedNewsAdapter);

        Observable<NewsItemListKey> newsItemListKeyObservable = createNewsItemListKeyObservable()
                .share() // convert to hot observable coz replaceNewsItemListView can be call more than once
                .distinctUntilChanged(NewsItemListKey::hashCode);

        newsSubject = PublishSubject.create();
        newsSubject.subscribe(new EmptyObserver<List<NewsItemDTOKey>>()
        {
            @Override public void onNext(List<NewsItemDTOKey> args)
            {
                mFeaturedNewsAdapter.setItems(args);
            }
        });

        newsSubject.subscribe(new UpdateUIObserver());

        paginatedNewsItemListKeyObservable = PaginationObservable.createFromRange(newsItemListKeyObservable, (Func1<NewsItemListKey, Observable<List<NewsItemDTOKey>>>)
                key -> newsServiceWrapper.getNewsRx(key)
                        .doOnNext(newsItemCompactDTOPaginatedDTO -> lastPaginationInfoDTO = newsItemCompactDTOPaginatedDTO.getPagination())
                        .flatMapIterable(PaginatedDTO::getData)
                        .map(NewsItemCompactDTO::getDiscussionKey)
                        .toList());

        activateNewsItemListView();
    }

    private void activateNewsItemListView()
    {
        progressBar.setVisibility(View.VISIBLE);
        newsSubscription = rxLoaderManager.create(newsItemListKey, paginatedNewsItemListKeyObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsSubject);
    }

    protected final void replaceNewsItemListView(NewsItemListKey newKey)
    {
        if (!newsItemListKey.equals(newKey))
        {
            NewsItemListKey oldKey = newsItemListKey;
            newsItemListKey = newKey;
            activateNewsItemListView();
            rxLoaderManager.remove(oldKey);
        }
    }

    protected Observable<NewsItemListKey> createNewsItemListKeyObservable()
    {
        Observable<NewsItemListKey> pullFromStartObservable = Observable.create(subscriber ->
                swipeRefreshLayout.setOnRefreshListener(() ->
                        subscriber.onNext(NewsItemListKeyHelper.copy(newsItemListKey, new PaginationDTO(1, newsItemListKey.perPage)))
                ));

        Observable<NewsItemListKey> pullFromBottomObservable = Observable.create(subscriber ->
                mNewsListView.setOnScrollListener(
                        new MultiScrollListener(scrollListener, dashboardBottomTabsScrollListener, new OnScrollOperator(subscriber))));
        return Observable.merge(pullFromBottomObservable, pullFromStartObservable)
                .startWith(NewsItemListKeyHelper.copy(newsItemListKey, new PaginationDTO(1, newsItemListKey.perPage)));
    }

    public final void setScrollListener(AbsListView.OnScrollListener scrollListener)
    {
        this.scrollListener = scrollListener;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onDestroyView()
    {
        newsSubscription.unsubscribe();
        rxLoaderManager.remove(newsItemListKey);
        super.onDestroyView();
    }

    private class UpdateUIObserver implements rx.Observer<List<NewsItemDTOKey>>
    {
        private void updateUI()
        {
            progressBar.setVisibility(View.INVISIBLE);
            mBottomLoadingView.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
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

    private class OnScrollOperator implements AbsListView.OnScrollListener
    {
        private final Subscriber<? super NewsItemListKey> subscriber;

        public OnScrollOperator(Subscriber<? super NewsItemListKey> subscriber)
        {
            this.subscriber = subscriber;
        }

        @Override public void onScrollStateChanged(AbsListView view, int scrollState) { }

        @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount > 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1))
            {
                if (newsItemListKey != null && lastPaginationInfoDTO != null)
                {
                    mBottomLoadingView.setVisibility(View.VISIBLE);
                    subscriber.onNext(NewsItemListKeyHelper.copy(newsItemListKey, lastPaginationInfoDTO.next));
                }
            }
        }
    }
}
