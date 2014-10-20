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
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.timeline.SubTimelineAdapter;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.List;
import javax.inject.Inject;

public class DiscoveryDiscussionFragment extends Fragment
{
    private static final int TIMELINE_LOADER_ID = 0;

    @InjectView(R.id.content_wrapper) BetterViewAnimator mContentWrapper;
    @InjectView(R.id.timeline_list_view) PullToRefreshListView mTimelineListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

    private int mDisplayedViewId;
    private SubTimelineAdapter mTimelineAdapter;
    private ProgressBar mBottomLoadingView;
    @Inject CurrentUserId currentUserId;
    private DiscoveryDiscussionAdapter discoveryDiscussionAdapter;

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

        mTimelineAdapter = new SubTimelineAdapter(getActivity(), TIMELINE_LOADER_ID, R.layout.timeline_item_view);
        mTimelineAdapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<TimelineItemDTOKey>()
        {
            @Override protected void onLoadFinished(ListLoader<TimelineItemDTOKey> loader, List<TimelineItemDTOKey> data)
            {
                mContentWrapper.setDisplayedChildByLayoutId(mTimelineListView.getId());
                mBottomLoadingView.setVisibility(View.GONE);
                mTimelineListView.onRefreshComplete();
            }

            @Override protected ListLoader<TimelineItemDTOKey> onCreateLoader(Bundle args)
            {
                TimelineListLoader timelineListLoader =
                        new TimelineListLoader(getActivity(), currentUserId.toUserBaseKey(), UserTimelineService.TimelineSection.Hot);
                timelineListLoader.setPerPage(Constants.TIMELINE_ITEM_PER_PAGE);
                return timelineListLoader;
            }
        });

        mBottomLoadingView = new ProgressBar(getActivity());
        mTimelineListView.setAdapter(mTimelineAdapter);
        mTimelineListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsScrollListener));
        mTimelineListView.getRefreshableView().addFooterView(mBottomLoadingView);
        mTimelineListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override public void onLastItemVisible()
            {
                mTimelineAdapter.getLoader().loadPrevious();
                mBottomLoadingView.setVisibility(View.VISIBLE);
            }
        });
        mTimelineListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase)
            {
                switch (listViewPullToRefreshBase.getCurrentMode())
                {
                    case PULL_FROM_START:
                        mTimelineAdapter.getLoader().loadNext();
                        break;
                    case PULL_FROM_END:
                        mTimelineAdapter.getLoader().loadPrevious();
                        break;
                }
            }
        });
        getActivity().getSupportLoaderManager().initLoader(
                mTimelineAdapter.getLoaderId(), null,
                mTimelineAdapter.getLoaderCallback());
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

    @Override public void onPause()
    {
        mDisplayedViewId = mContentWrapper.getDisplayedChildLayoutId();
        super.onPause();
    }
}
