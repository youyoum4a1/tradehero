package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListFeaturedKey;
import com.tradehero.th.api.news.key.NewsItemListGlobalKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListKeyHelper;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.news.NewsHeadlineViewLinear;
import com.tradehero.th.fragments.news.NewsWebFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.rx.PaginationObservable;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.content.ContentObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static butterknife.ButterKnife.findById;
import static com.tradehero.th.rx.view.list.ListViewObservable.createNearEndScrollOperator;

public class NewsHeadlineFragment extends Fragment
{
    private static final String NEWS_TYPE_KEY = NewsHeadlineFragment.class.getName() + ".newsType";
    public static final String REGION_CHANGED = NewsHeadlineFragment.class + ".regionChanged";

    @Inject Locale locale;
    @Inject @RegionalNews CountryLanguagePreference countryLanguagePreference;

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.discovery_news_list) ListView mNewsListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private Subscription newsSubscription;
    private PaginationInfoDTO lastPaginationInfoDTO;
    private ProgressBar mBottomLoadingView;
    private PublishSubject<List<NewsItemCompactDTO>> newsSubject;
    private AbsListView.OnScrollListener scrollListener;
    private Observable<PaginationDTO> paginationObservable;
    protected CompositeSubscription subscriptions;
    protected NewsType newsType;

    @OnItemClick(R.id.discovery_news_list) void handleNewsItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        NewsItemCompactDTO newsItemDTO = (NewsItemCompactDTO) parent.getItemAtPosition(position);

        if (newsItemDTO != null && newsItemDTO.url != null)
        {
            Bundle bundle = new Bundle();
            NewsWebFragment.putPreviousScreen(bundle, newsType.analyticsName);
            NewsWebFragment.putUrl(bundle, newsItemDTO.url);
            navigator.get().pushFragment(NewsWebFragment.class, bundle);
        }
    }

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

    protected NewsItemListKey newsItemListKey;

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
                newsType = NewsType.values()[newsTypeOrdinal];
                newsItemListKey = newsItemListKeyFromNewsType(newsType);
            }
        }
        if (newsType == NewsType.Region)
        {
            if (countryLanguagePreference.isSet())
            {
                newsItemListKey = newsItemListKeyFromPref();
            }
            else
            {
                newsItemListKey = new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
            }
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_news, container, false);
        initView(view);
        return view;
    }

    protected void initView(View view)
    {
        ButterKnife.inject(this, view);
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.discovery_news_carousel_height);
        scrollListener = new QuickReturnListViewOnScrollListener(
                QuickReturnType.HEADER, findById(getActivity(), R.id.news_carousel_wrapper), -headerHeight, null, 0);

        final ArrayDTOAdapter<NewsItemCompactDTO, NewsHeadlineViewLinear> mNewsAdapter =
                new ArrayDTOAdapter<>(getActivity(), R.layout.news_headline_item_view);

        mBottomLoadingView = new ProgressBar(getActivity());
        mBottomLoadingView.setVisibility(View.GONE);
        mNewsListView.addFooterView(mBottomLoadingView);
        mNewsListView.setAdapter(mNewsAdapter);
        swipeRefreshLayout.setProgressViewOffset(false,
                headerHeight,
                headerHeight + (int) getResources().getDimension(R.dimen.discovery_news_swipe_indicator_height));

        final Random random = new Random();
        paginationObservable = createPaginationObservable()
                // convert to hot observable coz replaceNewsItemListView can be call more than once
                .share()
                        // pulling down from top always refresh the list
                .distinctUntilChanged(new Func1<PaginationDTO, Integer>()
                {
                    @Override public Integer call(PaginationDTO key)
                    {
                        return key.hashCode() + (key.page != 1 ? 0 : random.nextInt());
                    }
                });

        newsSubject = PublishSubject.create();
        subscriptions = new CompositeSubscription();
        subscriptions.add(newsSubject.subscribe(
                new Action1<List<NewsItemCompactDTO>>()
                {
                    @Override public void call(List<NewsItemCompactDTO> newsItemCompactDTOs)
                    {
                        mNewsAdapter.setItems(newsItemCompactDTOs);
                    }
                },
                new TimberOnErrorAction("Gotcha")));
        subscriptions.add(newsSubject.subscribe(new UpdateUIObserver()));

        activateNewsItemListView();

        if (newsType == NewsType.Region)
        {
            subscriptions.add(
                    AppObservable.bindFragment(
                            this,
                            ContentObservable.fromLocalBroadcast(getActivity(), new IntentFilter(REGION_CHANGED))
                                    .map(new Func1<Intent, NewsItemListKey>()
                                    {
                                        @Override public NewsItemListKey call(Intent intent)
                                        {
                                            return NewsHeadlineFragment.this.newsItemListKeyFromPref();
                                        }
                                    })
                                    .subscribeOn(Schedulers.io()))
                            .onErrorResumeNext(Observable.<NewsItemListKey>empty())
                            .subscribe(new Action1<NewsItemListKey>()
                            {
                                @Override public void call(NewsItemListKey itemListKey)
                                {
                                    NewsHeadlineFragment.this.replaceNewsItemListView(itemListKey);
                                }
                            }, new ToastOnErrorAction()));
        }
    }

    private Observable<PaginationDTO> createPaginationObservable()
    {
        Observable<PaginationDTO> pullFromStartObservable = Observable.create(
                new Observable.OnSubscribe<PaginationDTO>()
                {
                    @Override public void call(final Subscriber<? super PaginationDTO> subscriber)
                    {
                        swipeRefreshLayout.setOnRefreshListener(
                                new SwipeRefreshLayout.OnRefreshListener()
                                {
                                    @Override public void onRefresh()
                                    {
                                        subscriber.onNext(new PaginationDTO(1, newsItemListKey.perPage));
                                    }
                                }
                        );
                    }
                });

        Observable<PaginationDTO> pullFromBottomObservable = Observable.create(
                new Observable.OnSubscribe<PaginationDTO>()
                {
                    @Override public void call(Subscriber<? super PaginationDTO> subscriber)
                    {
                        mNewsListView.setOnScrollListener(new MultiScrollListener(
                                scrollListener,
                                dashboardBottomTabsScrollListener,
                                createNearEndScrollOperator(
                                        subscriber,
                                        new Func0<PaginationDTO>()
                                        {
                                            @Override public PaginationDTO call()
                                            {
                                                if (newsItemListKey != null && lastPaginationInfoDTO != null)
                                                {
                                                    return lastPaginationInfoDTO.next;
                                                }
                                                return null;
                                            }
                                        })));
                    }
                })
                .doOnNext(new Action1<PaginationDTO>()
                {
                    @Override public void call(PaginationDTO paginationDTO)
                    {
                        mBottomLoadingView.setVisibility(View.VISIBLE);
                    }
                });
        return Observable.merge(pullFromBottomObservable, pullFromStartObservable)
                .subscribeOn(AndroidSchedulers.mainThread())
                .startWith(new PaginationDTO(1, newsItemListKey.perPage));
    }

    private Observable<List<NewsItemCompactDTO>> createNewsListKeyPaginationObservable()
    {
        return PaginationObservable.createFromRange(paginationObservable, (Func1<PaginationDTO, Observable<List<NewsItemCompactDTO>>>)
                        new Func1<PaginationDTO, Observable<List<NewsItemCompactDTO>>>()
                        {
                            @Override public Observable<List<NewsItemCompactDTO>> call(PaginationDTO key)
                            {
                                return newsServiceWrapper.getNewsRx(NewsItemListKeyHelper.copy(newsItemListKey, key))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .flatMapIterable(new Func1<PaginatedDTO<NewsItemCompactDTO>, Iterable<? extends NewsItemCompactDTO>>()
                                        {
                                            @Override public Iterable<? extends NewsItemCompactDTO> call(
                                                    PaginatedDTO<NewsItemCompactDTO> paginatedDTO)
                                            {
                                                lastPaginationInfoDTO =
                                                        paginatedDTO.getPagination();
                                                return paginatedDTO.getData();
                                            }
                                        })
                                        .toList();
                            }
                        }
        );
    }

    private void activateNewsItemListView()
    {
        progressBar.setVisibility(View.VISIBLE);
        newsSubscription = rxLoaderManager.create(newsItemListKey, createNewsListKeyPaginationObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<NewsItemCompactDTO>>empty())
                .subscribe(newsSubject);
    }

    protected final void replaceNewsItemListView(NewsItemListKey newKey)
    {
        if (!newsItemListKey.equals(newKey))
        {
            if (newsSubscription != null)
            {
                newsSubscription.unsubscribe();
            }
            rxLoaderManager.remove(newsItemListKey);
            newsItemListKey = newKey;
            activateNewsItemListView();
        }
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onDestroyView()
    {
        newsSubscription.unsubscribe();
        newsSubscription = null;
        subscriptions.unsubscribe();
        subscriptions = null;
        rxLoaderManager.remove(newsItemListKey);
        super.onDestroyView();
    }

    private class UpdateUIObserver implements rx.Observer<List<NewsItemCompactDTO>>
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

        @Override public void onNext(List<NewsItemCompactDTO> newsItemDTOKeys)
        {
            updateUI();
        }
    }

    private NewsItemListRegionalKey newsItemListKeyFromPref()
    {
        CountryLanguagePairDTO countryLanguagePairDTO = countryLanguagePreference.get();
        return new NewsItemListRegionalKey(countryLanguagePairDTO.countryCode, countryLanguagePairDTO.languageCode, null, null);
    }
}
