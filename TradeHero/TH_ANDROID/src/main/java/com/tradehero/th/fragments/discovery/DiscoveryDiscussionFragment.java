package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.tradehero.th.utils.Constants.TIMELINE_ITEM_PER_PAGE;

public class DiscoveryDiscussionFragment extends Fragment
{
    private static final String DISCOVERY_LIST_LOADER_ID = DiscoveryDiscussionFragment.class.getName() + ".discoveryList";

    @InjectView(R.id.timeline_list_view) PullToRefreshListView mTimelineListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject CurrentUserId currentUserId;
    @Inject UserTimelineServiceWrapper userTimelineServiceWrapper;
    @Inject ToastOnErrorAction toastOnErrorAction;

    private ProgressBar mBottomLoadingView;

    private DiscoveryDiscussionAdapter discoveryDiscussionAdapter;
    private Subscription timelineSubscription;

    private RangeDTO currentRangeDTO = new RangeDTO(TIMELINE_ITEM_PER_PAGE, null, null);

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        discoveryDiscussionAdapter = new DiscoveryDiscussionAdapter(getActivity(), R.layout.timeline_item_view);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_discussion, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mBottomLoadingView = new ProgressBar(getActivity());
        mTimelineListView.setAdapter(discoveryDiscussionAdapter);
        mTimelineListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsScrollListener));
        mTimelineListView.getRefreshableView().addFooterView(mBottomLoadingView);

        PublishSubject<List<TimelineItemDTOKey>> timelineSubject = PublishSubject.create();
        // emit item on pull up/down or when reaching the bottom of the listview
        Observable<RangeDTO> timelineRefreshRangeObservable = Observable
                .create((Observable.OnSubscribe<PullToRefreshBase.Mode>) subscriber -> {
                    mTimelineListView.setOnRefreshListener(
                            listViewPullToRefreshBase -> subscriber.onNext(listViewPullToRefreshBase.getCurrentMode()));

                    mTimelineListView.setOnLastItemVisibleListener(() -> {
                        if (!mTimelineListView.isRefreshing() && !discoveryDiscussionAdapter.isEmpty())
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
                            return RangeDTO.create(currentRangeDTO.maxCount, currentRangeDTO.minId, null);
                        case PULL_FROM_START:
                        default:
                            return RangeDTO.create(currentRangeDTO.maxCount, null, currentRangeDTO.maxId);
                    }
                })
                .startWith(RangeDTO.create(TIMELINE_ITEM_PER_PAGE, null, null));

        timelineSubject.subscribe(new RefreshCompleteObserver());
        timelineSubject.subscribe(discoveryDiscussionAdapter::setItems);
        timelineSubject.subscribe(new UpdateRangeObserver());

        timelineSubscription = rxLoaderManager.create(DISCOVERY_LIST_LOADER_ID,
                PaginationObservable.createFromRange(timelineRefreshRangeObservable, (Func1<RangeDTO, Observable<List<TimelineItemDTOKey>>>)
                        rangeDTO -> userTimelineServiceWrapper.getTimelineBySectionRx(TimelineSection.Hot, currentUserId.toUserBaseKey(), rangeDTO)
                                .map(TimelineDTO::getEnhancedItems)
                                .flatMap(Observable::from)
                                .map(TimelineItemDTO::getDiscussionKey)
                                .toList()))
                // gotta do error handling here when applicable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(toastOnErrorAction)
                .onErrorResumeNext(Observable.empty())
                .doOnCompleted(mTimelineListView::onRefreshComplete)
                .subscribe(timelineSubject);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        mTimelineListView.setOnLastItemVisibleListener(null);
        timelineSubscription.unsubscribe();
        rxLoaderManager.remove(DISCOVERY_LIST_LOADER_ID);
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
            mTimelineListView.onRefreshComplete();
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
}
