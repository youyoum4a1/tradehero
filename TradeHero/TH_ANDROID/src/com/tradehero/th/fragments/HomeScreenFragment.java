package com.tradehero.th.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimelineAdapter;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.widget.user.ProfileView;

public class HomeScreenFragment extends SherlockFragment
{
    private PullToRefreshListView userTimelineItemList;
    private UserProfileDTO profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.profile_screen, container, false);
        profile = THUser.getCurrentUser();
        if (profile != null)
        {
            _initView(view);
        }
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void _initView(View view)
    {
        createTimelineRequest();

        ProfileView profileView = (ProfileView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_detail, null);
        profileView.display(profile);

        userTimelineItemList = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        userTimelineItemList.getRefreshableView().addHeaderView(profileView);
        registerForContextMenu(userTimelineItemList);
        //createTimelineAutoFocus();

        getSherlockActivity().getSupportActionBar().setTitle(profile.displayName);

    }

    private void createTimelineAutoFocus()
    {
        userTimelineItemList.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            private View lastVisibleView = null;
            @Override public void onScrollStateChanged(AbsListView absListView, int state)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                View middleView = view.getChildAt(firstVisibleItem+visibleItemCount/2);
                if (middleView == lastVisibleView)
                {
                    return;
                }
                if (middleView != null)
                {
                    middleView.setBackgroundColor(Color.RED);
                    if (lastVisibleView != null)
                    {
                        lastVisibleView.setBackgroundColor(getResources().getColor(R.color.home_screen_list_item_background));
                    }
                    lastVisibleView = middleView;
                }
            }
        });
    }

    private void createTimelineRequest()
    {
        NetworkEngine.createService(UserTimelineService.class)
            .getTimeline(profile.id, 42, new THCallback<TimelineDTO>()
            {
                @Override protected void success(TimelineDTO timelineDTO, THResponse thResponse)
                {
                    userTimelineItemList.setVisibility(View.VISIBLE);
                    refreshTimeline(timelineDTO);
                }

                @Override protected void failure(THException ex)
                {
                }
            });
    }

    private void refreshTimeline(TimelineDTO timelineDTO)
    {
        userTimelineItemList.setAdapter(createTimelineAdapter(timelineDTO));
    }

    private ListAdapter createTimelineAdapter(TimelineDTO timelineDTO)
    {
        return new TimelineAdapter(getActivity(), getActivity().getLayoutInflater(), timelineDTO);
    }
}
