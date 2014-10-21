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
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserTimelineServiceRx;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.tradehero.th.network.service.UserTimelineServiceRx.TimelineSection.Hot;

public class DiscoveryDiscussionFragment extends Fragment
{
    @InjectView(R.id.content_wrapper) BetterViewAnimator mContentWrapper;
    @InjectView(R.id.timeline_list_view) PullToRefreshListView mTimelineListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject CurrentUserId currentUserId;
    @Inject UserTimelineServiceRx userTimelineServiceRx;

    private int mDisplayedViewId;
    private ProgressBar mBottomLoadingView;

    private DiscoveryDiscussionAdapter discoveryDiscussionAdapter;
    private Subscription timelineSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        discoveryDiscussionAdapter = new DiscoveryDiscussionAdapter(getActivity(), R.layout.timeline_item_view);
        timelineSubscription = rxLoaderManager.create(currentUserId.toUserBaseKey(), timelineTask())
                .subscribe(intoAdapter());
    }

    private Action1<List<TimelineItemDTOKey>> intoAdapter()
    {
        return new Action1<List<TimelineItemDTOKey>>()
        {
            @Override public void call(List<TimelineItemDTOKey> timelineItemDTOKeys)
            {
                discoveryDiscussionAdapter.setItems(timelineItemDTOKeys);
                mContentWrapper.setDisplayedChildByLayoutId(mTimelineListView.getId());
            }
        };
    }

    private Observable<List<TimelineItemDTOKey>> timelineTask()
    {
        return userTimelineServiceRx.getTimelineRx(Hot, currentUserId.get(), null, null, null)
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
        mTimelineListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override public void onLastItemVisible()
            {
                mBottomLoadingView.setVisibility(View.VISIBLE);
            }
        });

        // emit item on pull up/down
        // mTimelineListView.setOnRefreshListener(null);
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

    @Override public void onResume()
    {
        super.onResume();

        if (mDisplayedViewId > 0)
        {
            mContentWrapper.setDisplayedChildByLayoutId(mDisplayedViewId);
        }
    }

    @Override public void onDestroy()
    {
        timelineSubscription.unsubscribe();
        if (isDetached())
        {
            rxLoaderManager.remove(currentUserId.toUserBaseKey());
        }
    }

    @Override public void onPause()
    {
        mDisplayedViewId = mContentWrapper.getDisplayedChildLayoutId();
        super.onPause();
    }
}
