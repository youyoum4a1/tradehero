package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.loaders.ItemWithComparableId;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimelineAdapter;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.widget.timeline.TimelineListView;
import com.tradehero.th.widget.user.ProfileView;
import java.util.List;

public class HomeScreenFragment extends ItemListFragment<TimelineItem>
{
    private UserProfileDTO profile;
    private TimelineAdapter timelineAdapter;
    private TimelineListView timelineListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.profile_screen, container, false);
        profile = THUser.getCurrentUser();
        if (profile != null)
        {
            initView(view);
        }
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view)
    {
        ProfileView profileView = (ProfileView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_detail, null);
        profileView.display(profile);

        timelineListView = (TimelineListView) view.findViewById(R.id.pull_refresh_list);
        timelineListView.addHeaderView(profileView);
        TimelineAdapter timelineAdapter = createTimelineAdapter();
        timelineListView.setAdapter(timelineAdapter);
        timelineListView.setOnRefreshListener(timelineAdapter);
        timelineListView.setOnScrollListener(timelineAdapter);

        setListView(timelineListView.getRefreshableView());
        registerForContextMenu(timelineListView);

        getSherlockActivity().getSupportActionBar().setTitle(profile.displayName);
    }

    private TimelineAdapter createTimelineAdapter()
    {
        timelineAdapter = new TimelineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.user_profile_timeline_item);
        timelineAdapter.setLoader(createTimelineLoader());
        return timelineAdapter;
    }

    private TimelinePagedItemListLoader createTimelineLoader()
    {
        TimelinePagedItemListLoader timelineLoader = new TimelinePagedItemListLoader(getActivity());
        timelineLoader.setOwnerId(profile.id);
        return timelineLoader;
    }

    //<editor-fold desc="Loaders methods">

    @Override public void onLoadFinished(Loader<List<TimelineItem>> listLoader, List<TimelineItem> items)
    {
        super.onLoadFinished(listLoader, items);
        timelineListView.onRefreshComplete();
    }

    @Override public Loader<List<TimelineItem>> onCreateLoader(int id, Bundle bundle)
    {
        return timelineAdapter == null ? null : timelineAdapter.getLoader();
    }
    //</editor-fold>
}
