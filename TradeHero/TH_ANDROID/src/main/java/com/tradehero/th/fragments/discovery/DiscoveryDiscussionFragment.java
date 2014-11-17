package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.rx.PaginationObservable;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.observables.Assertions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.tradehero.th.utils.Constants.TIMELINE_ITEM_PER_PAGE;

public class DiscoveryDiscussionFragment extends Fragment
{
    private static final String DISCOVERY_LIST_LOADER_ID = DiscoveryDiscussionFragment.class.getName() + ".discoveryList";

    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.timeline_list_view) ListView mTimelineListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject CurrentUserId currentUserId;
    @Inject UserTimelineServiceWrapper userTimelineServiceWrapper;
    @Inject ToastOnErrorAction toastOnErrorAction;

    private ProgressBar mBottomLoadingView;

    private DiscoveryDiscussionAdapter discoveryDiscussionAdapter;
    @NonNull private CompositeSubscription timelineSubscriptions;

    private RangeDTO currentRangeDTO = new RangeDTO(TIMELINE_ITEM_PER_PAGE, null, null);
    private int mTotalHeadersAndFooters;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        discoveryDiscussionAdapter = new DiscoveryDiscussionAdapter(getActivity(), R.layout.timeline_item_view);
        timelineSubscriptions = new CompositeSubscription();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_discussion, container, false);
        initView(view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_post_discussion, menu);
        MenuItem postMenuButton = menu.findItem(R.id.discussion_edit_post);
        if (postMenuButton != null)
        {
            postMenuButton.setVisible(true);
        }
    }

    private Observable<RangeDTO> createPaginationObservable()
    {
        Observable<RangeDTO> pullFromStartObservable = Observable.create(subscriber ->
                swipeRefreshLayout.setOnRefreshListener(() ->
                                subscriber.onNext(RangeDTO.create(currentRangeDTO.maxCount, null, currentRangeDTO.maxId))
                ));

        Observable<RangeDTO> pullFromBottomObservable = Observable.create(subscriber ->
                mTimelineListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsScrollListener,
                        new OnScrollOperator(subscriber))));
        return Observable.merge(pullFromBottomObservable, pullFromStartObservable)
                .subscribeOn(AndroidSchedulers.mainThread())
                .startWith(RangeDTO.create(TIMELINE_ITEM_PER_PAGE, null, null));
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mBottomLoadingView = new ProgressBar(getActivity());
        mBottomLoadingView.setVisibility(View.INVISIBLE);
        mTimelineListView.addFooterView(mBottomLoadingView);
        mTimelineListView.setAdapter(discoveryDiscussionAdapter);
        mTotalHeadersAndFooters = mTimelineListView.getHeaderViewsCount() + mTimelineListView.getFooterViewsCount();

        PublishSubject<List<TimelineItemDTOKey>> timelineSubject = PublishSubject.create();
        timelineSubscriptions.add(timelineSubject.subscribe(new RefreshCompleteObserver()));
        timelineSubscriptions.add(timelineSubject.subscribe(discoveryDiscussionAdapter::setItems));
        timelineSubscriptions.add(timelineSubject.subscribe(new UpdateRangeObserver()));

        Observable<RangeDTO> timelineRefreshRangeObservable = createPaginationObservable();
        timelineSubscriptions.add(rxLoaderManager.create(
                DISCOVERY_LIST_LOADER_ID,
                PaginationObservable.createFromRange(timelineRefreshRangeObservable, (Func1<RangeDTO, Observable<List<TimelineItemDTOKey>>>)
                        rangeDTO -> userTimelineServiceWrapper.getTimelineBySectionRx(TimelineSection.Hot, currentUserId.toUserBaseKey(), rangeDTO)
                                .flatMapIterable(TimelineDTO::getEnhancedItems)
                                .map(TimelineItemDTO::getDiscussionKey)
                                .toList()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(toastOnErrorAction)
                .onErrorResumeNext(Observable.empty())
                .subscribe(timelineSubject));
    }

    @Override public void onDestroyView()
    {
        timelineSubscriptions.unsubscribe();
        rxLoaderManager.remove(DISCOVERY_LIST_LOADER_ID);
        super.onDestroyView();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    private class RefreshCompleteObserver implements Observer<List<TimelineItemDTOKey>>
    {
        private void refreshComplete()
        {
            mBottomLoadingView.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override public void onCompleted()
        {
            refreshComplete();
        }

        @Override public void onError(Throwable e)
        {
            refreshComplete();
        }

        @Override public void onNext(List<TimelineItemDTOKey> timelineItemDTOKeys)
        {
            refreshComplete();
        }
    }

    private class UpdateRangeObserver extends EmptyObserver<List<TimelineItemDTOKey>>
    {
        @Override public void onNext(List<TimelineItemDTOKey> timelineItemDTOKeys)
        {
            if (timelineItemDTOKeys != null && !timelineItemDTOKeys.isEmpty())
            {
                Integer max = timelineItemDTOKeys.get(0).id;
                Integer min = timelineItemDTOKeys.get(timelineItemDTOKeys.size() - 1).id;
                currentRangeDTO = RangeDTO.create(TIMELINE_ITEM_PER_PAGE, max, min);
            }
            else
            {
                currentRangeDTO = RangeDTO.create(TIMELINE_ITEM_PER_PAGE, null, null);
            }
        }
    }

    private class OnScrollOperator implements AbsListView.OnScrollListener
    {
        private final Subscriber<? super RangeDTO> subscriber;
        private boolean scrollStateChanged;

        public OnScrollOperator(Subscriber<? super RangeDTO> subscriber)
        {
            this.subscriber = subscriber;
        }

        @Override public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            scrollStateChanged = true;
        }

        @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            Assertions.assertUiThread();

            if (totalItemCount > mTotalHeadersAndFooters && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1))
            {
                if (currentRangeDTO != null && scrollStateChanged)
                {
                    scrollStateChanged = false;
                    mBottomLoadingView.setVisibility(View.VISIBLE);
                    subscriber.onNext(RangeDTO.create(currentRangeDTO.maxCount, currentRangeDTO.minId, null));
                }
            }
        }
    }
}
