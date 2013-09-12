package com.tradehero.th.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimelineAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.loaders.TimelineItemListLoader;
import com.tradehero.th.widget.user.ProfileView;
import java.util.List;

public class HomeScreenFragment extends ItemListFragment<TimelineItem>
{
    private PullToRefreshListView userTimelineItemList;
    private UserProfileDTO profile;
    private TimelineItemListLoader timelineLoader;

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
        //createTimelineRequest();

        ProfileView profileView = (ProfileView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_detail, null);
        profileView.display(profile);

        userTimelineItemList = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        userTimelineItemList.getRefreshableView().addHeaderView(profileView);
        userTimelineItemList.setOnRefreshListener(timelineLoader);
        userTimelineItemList.setAdapter(createTimelineAdapter());
        setListView(userTimelineItemList.getRefreshableView());

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

    private ListAdapter createTimelineAdapter()
    {
        return new TimelineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.user_profile_timeline_item);
    }

    //<editor-fold desc="Loaders methods">
    @Override public Loader<List<TimelineItem>> onCreateLoader(int id, Bundle bundle)
    {
        timelineLoader = new TimelineItemListLoader(getActivity());
        timelineLoader.setOwnerId(profile.id);
        timelineLoader.setItemPerPage(42);

        return timelineLoader;
    }
    //</editor-fold>
}
