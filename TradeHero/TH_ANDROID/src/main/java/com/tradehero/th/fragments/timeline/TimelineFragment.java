package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
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
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.widget.StepView;
import com.tradehero.th.widget.timeline.TimelineListView;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class TimelineFragment extends BaseFragment
        implements BaseFragment.ArgumentsChangeListener, StepView.StepProvider
{
    private TimelineAdapter timelineAdapter;

    private Bundle desiredArguments;
    protected UserBaseKey userBaseKey;
    protected UserProfileDTO profile;
    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected DTOCache.GetOrFetchTask<UserProfileDTO> userProfileCacheTask;
    private TimelineListView timelineListView;

    @Inject protected Lazy<UserProfileCache> userProfileCache;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.profile_screen, container, false);
        initView(view);
        return view;
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
        registerForContextMenu(timelineListView);
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
        switch (item.getItemId())
        {
            case R.id.menu_settings:
                Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                Bundle bundle = new Bundle();
                navigator.pushFragment(SettingsFragment.class, bundle);
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

        UserBaseKey newUserBaseKey = new UserBaseKey(desiredArguments.getInt(UserBaseKey.BUNDLE_KEY_KEY));
        linkWith(newUserBaseKey, true);

        Bundle loaderBundle = new Bundle(newUserBaseKey.getArgs());
        //loaderBundle.putInt();
        getLoaderManager().initLoader(0, loaderBundle, loaderCallback);
    }

    //<editor-fold desc="Display methods">
    @Override public void onDestroyView()
    {
        userProfileCacheListener = null;
        if (userProfileCacheTask != null)
        {
            userProfileCacheTask.forgetListener(true);
        }
        userProfileCacheTask = null;
        super.onDestroyView();
    }

    protected void linkWith(UserBaseKey userBaseKey, final boolean andDisplay)
    {
        this.userBaseKey = userBaseKey;
        if (userBaseKey != null)
        {
            timelineAdapter.getLoader().resetQuery();
            timelineAdapter.getLoader().setOwnerId(userBaseKey.key);

            UserProfileDTO cachedUserProfile = userProfileCache.get().get(userBaseKey);
            if (cachedUserProfile != null) // Testing with the cache like this is presumably faster
            {
                linkWith(cachedUserProfile, andDisplay);
            }
            else
            {
                userProfileCacheListener = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
                {
                    @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
                    {
                        linkWith(value, andDisplay);
                    }
                };
                if (userProfileCacheTask != null)
                {
                    userProfileCacheTask.forgetListener(true);
                }
                userProfileCacheTask = userProfileCache.get().getOrFetch(userBaseKey, false, userProfileCacheListener);
                userProfileCacheTask.execute();
            }
        }

        if (andDisplay)
        {
            // TODO
        }
    }

    protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.profile = userProfileDTO;
        if (andDisplay)
        {
            updateView();
        }
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
    //</editor-fold>

    //<editor-fold desc="Initial methods">
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
        return timelineLoader;
    }
    //</editor-fold>

    @Override public void onArgumentsChanged(Bundle args)
    {
        desiredArguments = args;
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

    private LoaderManager.LoaderCallbacks<List<TimelineItem>> loaderCallback = new LoaderManager.LoaderCallbacks<List<TimelineItem>>()
    {

        @Override public void onLoaderReset(Loader<List<TimelineItem>> listLoader)
        {
            // TODO more investigation
            if (timelineAdapter != null)
            {
                timelineAdapter.setItems(null);
            }
        }

        @Override public void onLoadFinished(Loader<List<TimelineItem>> listLoader, List<TimelineItem> items)
        {
            timelineAdapter.notifyDataSetChanged();
            timelineListView.onRefreshComplete();
        }

        @Override public Loader<List<TimelineItem>> onCreateLoader(int id, Bundle bundle)
        {
            return timelineAdapter == null ? null : timelineAdapter.getLoader();
        }

    };

}
