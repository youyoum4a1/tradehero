package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
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
import com.tradehero.th.widget.MultiScrollListener;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.tradehero.th.utils.Constants.TIMELINE_ITEM_PER_PAGE;

public class DiscoveryDiscussionFragment extends Fragment
{
    @InjectView(R.id.timeline_list_view) PullToRefreshListView mTimelineListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject CurrentUserId currentUserId;
    @Inject UserTimelineServiceWrapper userTimelineServiceWrapper;

    private ProgressBar mBottomLoadingView;

    private DiscoveryDiscussionAdapter discoveryDiscussionAdapter;
    private Subscription timelineSubscription;

    private RangeDTO currentRangeDTO = new RangeDTO(TIMELINE_ITEM_PER_PAGE, null, null);

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        discoveryDiscussionAdapter = new DiscoveryDiscussionAdapter(getActivity(), R.layout.timeline_item_view);
    }

    private Action1<List<TimelineItemDTOKey>> intoAdapter()
    {
        return new Action1<List<TimelineItemDTOKey>>()
        {
            @Override public void call(List<TimelineItemDTOKey> timelineItemDTOKeys)
            {
                discoveryDiscussionAdapter.setItems(timelineItemDTOKeys);
            }
        };
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
        // emit item on pull up/down
        Observable<RangeDTO> timelineRefreshRangeObservable = Observable
                .create(new Observable.OnSubscribe<PullToRefreshBase.Mode>()
                {
                    @Override public void call(final Subscriber<? super PullToRefreshBase.Mode> subscriber)
                    {
                        mTimelineListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
                        {
                            @Override public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase)
                            {
                                subscriber.onNext(listViewPullToRefreshBase.getCurrentMode());
                            }
                        });

                        mTimelineListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
                        {
                            @Override public void onLastItemVisible()
                            {
                                if (!mTimelineListView.isRefreshing() && !discoveryDiscussionAdapter.isEmpty())
                                {
                                    mBottomLoadingView.setVisibility(View.VISIBLE);
                                    subscriber.onNext(PullToRefreshBase.Mode.PULL_FROM_END);
                                }
                            }
                        });
                    }
                })
                .map(new Func1<PullToRefreshBase.Mode, RangeDTO>()
                {
                    @Override public RangeDTO call(PullToRefreshBase.Mode mode)
                    {
                        switch (mode)
                        {
                            case PULL_FROM_END:
                                return RangeDTO.create(currentRangeDTO.maxCount, currentRangeDTO.minId, null);
                            case PULL_FROM_START:
                            default:
                                return RangeDTO.create(currentRangeDTO.maxCount, null, currentRangeDTO.maxId);
                        }
                    }
                })
                .startWith(RangeDTO.create(TIMELINE_ITEM_PER_PAGE, null, null));

        timelineSubject.subscribe(intoAdapter());
        timelineSubject.subscribe(new RefreshCompleteObserver());
        timelineSubject.subscribe(new UpdateRangeObserver());

        timelineSubscription = rxLoaderManager.create(currentUserId.toUserBaseKey(),
                PaginationObservable.createFromRange(timelineRefreshRangeObservable, new Func1<RangeDTO, Observable<List<TimelineItemDTOKey>>>()
                {
                    @Override public Observable<List<TimelineItemDTOKey>> call(RangeDTO rangeDTO)
                    {
                        return userTimelineServiceWrapper.getTimelineBySectionRx(TimelineSection.Hot, currentUserId.toUserBaseKey(), rangeDTO)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Func1<TimelineDTO, List<TimelineItemDTO>>()
                                {
                                    @Override public List<TimelineItemDTO> call(TimelineDTO timelineDTO)
                                    {
                                        return timelineDTO.getEnhancedItems();
                                    }
                                })
                                .flatMap(new Func1<List<TimelineItemDTO>, Observable<TimelineItemDTO>>()
                                {
                                    @Override public Observable<TimelineItemDTO> call(List<TimelineItemDTO> timelineItemDTOs)
                                    {
                                        return Observable.from(timelineItemDTOs);
                                    }
                                })
                                .map(new Func1<TimelineItemDTO, TimelineItemDTOKey>()
                                {
                                    @Override public TimelineItemDTOKey call(TimelineItemDTO timelineItemDTO)
                                    {
                                        return timelineItemDTO.getDiscussionKey();
                                    }
                                })
                                .toList();
                    }
                }))
                .subscribe(timelineSubject);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        mTimelineListView.setOnLastItemVisibleListener(null);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        timelineSubscription.unsubscribe();
        if (isDetached())
        {
            rxLoaderManager.remove(currentUserId.toUserBaseKey());
        }
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

    private class UpdateRangeObserver implements Action1<List<TimelineItemDTOKey>>
    {
        @Override public void call(List<TimelineItemDTOKey> timelineItemDTOKeys)
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
