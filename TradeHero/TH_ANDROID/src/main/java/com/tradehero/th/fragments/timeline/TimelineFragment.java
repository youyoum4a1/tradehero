package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.widget.StepView;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class TimelineFragment extends BaseFragment
        implements StepView.StepProvider, PortfolioRequestListener
{
    public static final String TAG = TimelineFragment.class.getSimpleName();

    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    private TimelineAdapter timelineAdapter;
    private TimelineListView timelineListView;

    protected UserBaseKey userBaseKey;
    protected UserProfileDTO profile;
    protected OwnedPortfolioIdList portfolioIdList;

    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected DTOCache.GetOrFetchTask<UserProfileDTO> userProfileCacheTask;
    protected DTOCache.Listener<UserBaseKey, OwnedPortfolioIdList> portfolioCompactListCacheListener;
    protected DTOCache.GetOrFetchTask<OwnedPortfolioIdList> portfolioCompactListCacheTask;
    private StepView stepView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_screen, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        timelineListView = (TimelineListView) view.findViewById(R.id.pull_refresh_list);
        timelineListView.setEmptyView(view.findViewById(android.R.id.empty));
        final ListView refreshableListView = timelineListView.getRefreshableView();
        refreshableListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        if (timelineAdapter == null)
        {
            timelineAdapter = createTimelineAdapter();
        }
        timelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                refreshableListView.setItemChecked(position, true);
            }
        });

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        UserBaseKey newUserBaseKey = new UserBaseKey(getArguments().getInt(UserBaseKey.BUNDLE_KEY_KEY));
        linkWith(newUserBaseKey, true);

        Bundle loaderBundle = new Bundle(newUserBaseKey.getArgs());
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

        portfolioCompactListCacheListener = null;
        if (portfolioCompactListCacheTask != null)
        {
            portfolioCompactListCacheTask.forgetListener(true);
        }
        portfolioCompactListCacheTask = null;
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

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        THToast.show(getString(R.string.error_fetch_your_user_profile));
                        THLog.e(TAG, "Error fetching the user profile " + key, error);
                    }
                };
                if (userProfileCacheTask != null)
                {
                    userProfileCacheTask.forgetListener(true);
                }
                userProfileCacheTask = userProfileCache.get().getOrFetch(userBaseKey, false, userProfileCacheListener);
                userProfileCacheTask.execute();
            }

            OwnedPortfolioIdList cachedOwnedPortfolioIdList = portfolioCompactListCache.get().get(userBaseKey);
            if (cachedOwnedPortfolioIdList != null)
            {
                linkWith(cachedOwnedPortfolioIdList, andDisplay);
            }
            else
            {
                portfolioCompactListCacheListener = new DTOCache.Listener<UserBaseKey, OwnedPortfolioIdList>()
                {
                    @Override public void onDTOReceived(UserBaseKey key, OwnedPortfolioIdList value)
                    {
                        linkWith(value, andDisplay);
                    }

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        // We do not need to inform the player here
                        THLog.e(TAG, "Error fetching the list of portfolio " + key, error);
                    }
                };
                if (portfolioCompactListCacheTask != null)
                {
                    portfolioCompactListCacheTask.forgetListener(true);
                }
                portfolioCompactListCacheTask = portfolioCompactListCache.get().getOrFetch(userBaseKey, false, portfolioCompactListCacheListener);
                portfolioCompactListCacheTask.execute();
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

    private void linkWith(OwnedPortfolioIdList ownedPortfolioIdList, boolean andDisplay)
    {
        this.portfolioIdList = ownedPortfolioIdList;
        portfolioCache.get().autoFetch(ownedPortfolioIdList, (OwnedPortfolioId) null);

        if (andDisplay)
        {
            // Nothing to do
        }
    }

    protected void updateView()
    {
        // TODO retain state for stepView
        stepView = new StepView(getActivity(), getActivity().getLayoutInflater());
        stepView.setStepProvider(this);

        if (timelineListView.getRefreshableView().getHeaderViewsCount() == 1)
        {
            timelineListView.addHeaderView(stepView);
        }

        if (profile != null)
        {
            getSherlockActivity().getSupportActionBar().setTitle(profile.displayName);
        }
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

    @Override public View provideView(int step)
    {
        switch (step)
        {
            case 0:
                ProfileView profileView = (ProfileView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_detail, null);
                profileView.display(profile);
                profileView.setPortfolioRequestListener(this);
                return profileView;
            case 1:
                ProfileCompactView profileCompactView =
                        (ProfileCompactView) getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_compact, null);
                profileCompactView.display(profile);
                profileCompactView.setPortfolioRequestListener(this);
                return profileCompactView;
        }
        return null;
    }

    //<editor-fold desc="Loader Callback">
    private LoaderManager.LoaderCallbacks<List<TimelineItem>> loaderCallback = new LoaderManager.LoaderCallbacks<List<TimelineItem>>()
    {

        @Override public void onLoaderReset(Loader<List<TimelineItem>> listLoader)
        {
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
    //</editor-fold>

    //<editor-fold desc="PortfolioRequestListener">
    @Override public void onPortfolioRequested(OwnedPortfolioId ownedPortfolioId)
    {
        pushPositionListFragment(ownedPortfolioId);
    }

    @Override public void onDefaultPortfolioRequested()
    {
        if (portfolioIdList == null || portfolioIdList.size() < 1 || portfolioIdList.get(0) == null)
        {
            // HACK, instead we should test for Default title on PortfolioDTO
            THToast.show("Not enough data, try again");
        }
        else
        {
            pushPositionListFragment(portfolioIdList.get(0));
        }
    }
    //</editor-fold>

    private void pushPositionListFragment(OwnedPortfolioId ownedPortfolioId)
    {
        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
        navigator.pushFragment(PositionListFragment.class, ownedPortfolioId.getArgs());
    }
}
