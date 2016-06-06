package com.androidth.general.fragments.discovery;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import butterknife.Bind;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.BottomTabsQuickReturnListViewListener;
import com.androidth.general.R;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.news.key.NewsItemListFeaturedKey;
import com.androidth.general.api.news.key.NewsItemListGlobalKey;
import com.androidth.general.api.news.key.NewsItemListKey;
import com.androidth.general.api.news.key.NewsItemListKeyHelper;
import com.androidth.general.api.news.key.NewsItemListRegionalKey;
import com.androidth.general.api.news.key.NewsItemListSeekingAlphaKey;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.pagination.PaginationDTO;
import com.androidth.general.api.pagination.PaginationInfoDTO;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.androidth.general.fragments.discussion.DiscussionFragmentUtil;
import com.androidth.general.fragments.news.NewsHeadlineViewLinear;
import com.androidth.general.fragments.news.NewsWebFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.discussion.UserDiscussionAction;
import com.androidth.general.network.service.NewsServiceWrapper;
import com.androidth.general.rx.PaginationObservable;
import com.androidth.general.rx.RxLoaderManager;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.GraphicUtil;
import com.androidth.general.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.androidth.general.rx.view.list.ListViewObservable.createNearEndScrollOperator;

public class NewsHeadlineFragment extends Fragment
{
    private static final String NEWS_TYPE_KEY = NewsHeadlineFragment.class.getName() + ".newsType";
    public static final String REGION_CHANGED = NewsHeadlineFragment.class + ".regionChanged";

    @Inject Locale locale;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;

    @Bind(android.R.id.progress) ProgressBar progressBar;
    @Bind(R.id.discovery_news_list) ListView mNewsListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private ClipboardManager clipboardManager;
    private Subscription newsSubscription;
    private PaginationInfoDTO lastPaginationInfoDTO;
    private ProgressBar mBottomLoadingView;
    private PublishSubject<List<NewsItemCompactDTO>> newsSubject;
    private Observable<PaginationDTO> paginationObservable;
    protected CompositeSubscription subscriptions;
    protected NewsType newsType;
    protected DiscussionArrayAdapter newsAdapter;
    protected SubscriptionList onStopSubscriptions;

    @SuppressWarnings("unused")
    @OnItemClick(R.id.discovery_news_list) void handleNewsItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        NewsItemCompactDTO newsItemDTO = null;
        try
        {
            newsItemDTO = (NewsItemCompactDTO) ((NewsHeadlineViewLinear.DTO) parent.getItemAtPosition(position)).viewHolderDTO.discussionDTO;
        } catch (Exception e)
        {
            Timber.e(e, "Error:" + parent.getItemAtPosition(position));
        }

        if ((newsItemDTO != null) && (newsItemDTO.url != null))
        {
            Bundle bundle = new Bundle();
            NewsWebFragment.putPreviousScreen(bundle, newsType.analyticsName);
            NewsWebFragment.putUrl(bundle, newsItemDTO.url);
            NewsWebFragment.putNewsId(bundle, newsItemDTO.id);
            navigator.get().pushFragment(NewsWebFragment.class, bundle);
        }
    }

    @SuppressWarnings("unused")
    @OnItemLongClick(R.id.discovery_news_list) boolean handleNewsItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        try
        {
            NewsItemCompactDTO newsItemDTO = (NewsItemCompactDTO) ((NewsHeadlineViewLinear.DTO) parent.getItemAtPosition(position)).viewHolderDTO.discussionDTO;
            ClipData clip = ClipData.newPlainText("Video id", String.format("newsId:%d, url:%s", newsItemDTO.id, newsItemDTO.url));
            clipboardManager.setPrimaryClip(clip);
            THToast.show("Id and url copied to clipboard");
            return true;
        } catch (Exception e)
        {
            Timber.e(e, "Error:" + parent.getItemAtPosition(position));
            return false;
        }
    }

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

    protected NewsItemListKey newsItemListKey;

    @Nullable private NewsItemListKey newsItemListKeyFromNewsType(@NonNull NewsType newsType)
    {
        switch (newsType)
        {
            case MotleyFool:
                return new NewsItemListFeaturedKey(null, null);
            case Global:
                return new NewsItemListGlobalKey(null, null);
            case SeekingAlpha:
                return new NewsItemListSeekingAlphaKey(null, null);
            case Region:
                return new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
            default:
                return null;
        }
    }

    @NonNull public static NewsHeadlineFragment newInstance(@NonNull NewsType newsType)
    {
        NewsHeadlineFragment newsHeadlineFragment = new NewsHeadlineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NEWS_TYPE_KEY, newsType.ordinal());
        newsHeadlineFragment.setArguments(bundle);
        return newsHeadlineFragment;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
        newsAdapter =
                new DiscussionArrayAdapter(activity, R.layout.news_discovery_headline_item_view)
                {
                    @Override public AbstractDiscussionCompactItemViewLinear getView(int position, View convertView, ViewGroup viewGroup)
                    {
                        AbstractDiscussionCompactItemViewLinear view = super.getView(position, convertView, viewGroup);
                        GraphicUtil.setEvenOddBackground(position, view);
                        return view;
                    }
                };
        clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
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
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_news, container, false);
        initView(view);
        return view;
    }

    protected void initView(View view)
    {
        ButterKnife.bind(this, view);
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.discovery_news_carousel_height);

        mBottomLoadingView = new ProgressBar(getActivity());
        mBottomLoadingView.setVisibility(View.GONE);
        mNewsListView.addFooterView(mBottomLoadingView);
        mNewsListView.setAdapter(newsAdapter);
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
        subscriptions.add(newsSubject
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<List<NewsItemCompactDTO>, Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                {
                    @Override public Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                            List<NewsItemCompactDTO> newsItemCompactDTOs)
                    {
                        return viewDTOFactory.createNewsHeadlineViewLinearDTOs(newsItemCompactDTOs);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> dtos)
                            {
                                newsAdapter.setNotifyOnChange(false);
                                newsAdapter.clear();
                                newsAdapter.addAll(dtos);
                                newsAdapter.setNotifyOnChange(true);
                                newsAdapter.notifyDataSetChanged();
                            }
                        },
                        new TimberOnErrorAction1("Gotcha newsSubject in NewsHeadline passed error")));
        subscriptions.add(newsSubject.subscribe(new UpdateUIObserver()));

        activateNewsItemListView();
    }

    @NonNull private Observable<PaginationDTO> createPaginationObservable()
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

    @NonNull private Observable<List<NewsItemCompactDTO>> createNewsListKeyPaginationObservable()
    {
        return PaginationObservable.createFromRange(paginationObservable, new Func1<PaginationDTO, Observable<List<NewsItemCompactDTO>>>()
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

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions = new SubscriptionList();
        registerUserActions();
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

    protected void registerUserActions()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                newsAdapter.getUserActionObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserDiscussionAction, Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(UserDiscussionAction userDiscussionAction)
                    {
                        return discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                    }
                })
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                Timber.e(new Exception(), "Unhandled " + userDiscussionAction);
                            }
                        },
                        new TimberOnErrorAction1("Failed to register user actions")));
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
}
