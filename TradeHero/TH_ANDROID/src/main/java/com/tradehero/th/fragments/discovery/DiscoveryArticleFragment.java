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
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.ArticleServiceWrapper;
import com.tradehero.th.rx.PaginationObservable;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.tradehero.th.rx.view.list.ListViewObservable.createNearEndScrollOperator;

public class DiscoveryArticleFragment extends Fragment
{
    private static final String DISCOVERY_ARTICLE_LIST_ID = DiscoveryArticleFragment.class.getName() + ".articleList";

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.discovery_article_list) ListView mArticleListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private Subscription articleSubscription;
    private PaginationInfoDTO lastPaginationInfoDTO;
    private ProgressBar mBottomLoadingView;
    private PublishSubject<List<ArticleInfoDTO>> articlesSubject;
    private Observable<PaginationDTO> paginationObservable;
    protected CompositeSubscription subscriptions;
    private PaginationDTO currentPagination = new PaginationDTO(1, Constants.COMMON_ITEM_PER_PAGE);

    @OnItemClick(R.id.discovery_article_list)
    void handleNewsItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ArticleInfoDTO articleInfoDTO = (ArticleInfoDTO) parent.getItemAtPosition(position);

        if (articleInfoDTO != null && articleInfoDTO.url != null)
        {
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, articleInfoDTO.url);
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
    }

    @Inject ArticleServiceWrapper articleServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

    public DiscoveryArticleFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_articles, container, false);
        initView(view);
        return view;
    }

    protected void initView(View view)
    {
        ButterKnife.inject(this, view);
        ArrayDTOAdapter<ArticleInfoDTO, ArticleItemView> mArticleAdapter = new ArrayDTOAdapter<>(getActivity(), R.layout.article_item_view);

        mBottomLoadingView = new ProgressBar(getActivity());
        mBottomLoadingView.setVisibility(View.GONE);
        mArticleListView.addFooterView(mBottomLoadingView);
        mArticleListView.setAdapter(mArticleAdapter);

        final Random random = new Random();
        paginationObservable = createPaginationObservable()
                // convert to hot observable coz replaceNewsItemListView can be call more than once
                .share()
                // pulling down from top always refresh the list
                .distinctUntilChanged(key -> key.hashCode() + (key.page != 1 ? 0 : random.nextInt()));

        articlesSubject = PublishSubject.create();
        subscriptions = new CompositeSubscription();
        subscriptions.add(articlesSubject.subscribe(mArticleAdapter::setItems));
        subscriptions.add(articlesSubject.subscribe(new UpdateUIObserver()));

        activateArticleItemListView();
    }

    private Observable<PaginationDTO> createPaginationObservable()
    {
        Observable<PaginationDTO> pullFromStartObservable = Observable.create(subscriber ->
                swipeRefreshLayout.setOnRefreshListener(() ->
                                subscriber.onNext(new PaginationDTO(1, currentPagination.perPage))
                ));

        Observable<PaginationDTO> pullFromBottomObservable = Observable.create((Observable.OnSubscribe<PaginationDTO>) subscriber ->
                mArticleListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsScrollListener,
                        createNearEndScrollOperator(subscriber, () -> {
                            if (lastPaginationInfoDTO != null)
                            {
                                return lastPaginationInfoDTO.next;
                            }
                            return null;
                        }))))
                .doOnNext(o -> mBottomLoadingView.setVisibility(View.VISIBLE));
        return Observable.merge(pullFromBottomObservable, pullFromStartObservable)
                .subscribeOn(AndroidSchedulers.mainThread())
                .startWith(currentPagination);
    }

    private Observable<List<ArticleInfoDTO>> createArticleListKeyPaginationObservable()
    {
        return PaginationObservable.createFromRange(paginationObservable, (Func1<PaginationDTO, Observable<List<ArticleInfoDTO>>>)
                        key -> articleServiceWrapper.getAllArticlesRx(currentPagination)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(articleInfoDTOPaginatedDTO -> lastPaginationInfoDTO = articleInfoDTOPaginatedDTO.getPagination())
                                .flatMapIterable(PaginatedDTO::getData)
                                .toList()
        );
    }

    private void activateArticleItemListView()
    {
        progressBar.setVisibility(View.VISIBLE);
        articleSubscription = rxLoaderManager.create(DISCOVERY_ARTICLE_LIST_ID, createArticleListKeyPaginationObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.empty())
                .subscribe(articlesSubject);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onDestroyView()
    {
        articleSubscription.unsubscribe();
        articleSubscription = null;
        subscriptions.unsubscribe();
        subscriptions = null;
        rxLoaderManager.remove(DISCOVERY_ARTICLE_LIST_ID);
        super.onDestroyView();
    }

    private class UpdateUIObserver implements rx.Observer<List<ArticleInfoDTO>>
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

        @Override public void onNext(List<ArticleInfoDTO> articleInfoDTOs)
        {
            updateUI();
        }
    }
}
