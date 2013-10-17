package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimelineAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.ItemListFragment;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.widget.StepView;
import com.tradehero.th.widget.timeline.TimelineListView;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class TimelineFragment extends ItemListFragment<TimelineItem>
        implements DTOCache.Listener<UserBaseKey, UserProfileDTO>, BaseFragment.ArgumentsChangeListener, StepView.StepProvider
{
    public static final String USER_ID = "userId";
    private TimelineAdapter timelineAdapter;

    protected UserProfileDTO profile;
    protected int profileId;
    private TimelineListView timelineListView;

    @Inject protected Lazy<UserProfileCache> userProfileCache;

    private View view;
    private StepView stepView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.profile_screen, container, false);
        profileId = getArguments().getInt(USER_ID);

        setProfileId(profileId);
        return view;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu, menu);

        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        if (profile != null)
        {
            getSherlockActivity().getSupportActionBar().setTitle(profile.displayName);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    private void setProfileId(int profileId)
    {
        if (profileId != 0)
        {
            UserBaseKey baseKey = new UserBaseKey(profileId);
            userProfileCache.get()
                    .getOrFetch(baseKey, false, this).execute();
        }
        initView(view);
    }

    private void initView(View view)
    {
        timelineListView = (TimelineListView) view.findViewById(R.id.pull_refresh_list);
        if (timelineAdapter == null)
        {
            timelineAdapter = createTimelineAdapter();
        }
        timelineListView.setAdapter(timelineAdapter);
        timelineListView.setOnRefreshListener(timelineAdapter);
        timelineListView.setOnScrollListener(timelineAdapter);
        timelineListView.setOnLastItemVisibleListener(timelineAdapter);

        setListView(timelineListView.getRefreshableView());
        registerForContextMenu(timelineListView);
    }

    protected void updateView()
    {
        // TODO retain state for stepView
        StepView stepView = new StepView(getActivity(), getActivity().getLayoutInflater());
        stepView.setStepProvider(this);

        if (timelineListView.getRefreshableView().getHeaderViewsCount() == 1)
        {
            timelineListView.addHeaderView(stepView);
        }

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
        timelineLoader.setItemsPerPage(42);
        timelineLoader.setOwnerId(profileId);
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

    @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
    {
        profile = value;
        updateView();
    }

    @Override public void onArgumentsChanged(Bundle args)
    {
        profileId = args.getInt(USER_ID);

        timelineAdapter.getLoader().resetQuery();
        timelineAdapter.getLoader().setOwnerId(profileId);
        setProfileId(profileId);
    }

    @Override public View provideView(int step)
    {
        switch (step)
        {
            case 0:
                ProfileView profileView = (ProfileView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_detail, null);
                profileView.display(profile);
                return profileView;
            case 1:
                ProfileCompactView profileCompactView =
                        (ProfileCompactView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_compact, null);
                profileCompactView.display(profile);
                return profileCompactView;
        }
        return null;
    }
    //</editor-fold>
}
