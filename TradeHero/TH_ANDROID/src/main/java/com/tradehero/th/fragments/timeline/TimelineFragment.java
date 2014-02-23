package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineFragment extends BasePurchaseManagerFragment
        implements PortfolioRequestListener
{
    public static final String BUNDLE_KEY_SHOW_USER_ID =
            TimelineFragment.class.getName() + ".showUserId";

    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    @InjectView(R.id.timeline_list_view) PullToRefreshListView timelineListView;
    @InjectView(R.id.timeline_screen) BetterViewAnimator timelineScreen;

    private UserProfileView userProfileView;

    private TimelineAdapter timelineAdapter;

    protected ActionBar actionBar;

    protected UserBaseKey shownUserBaseKey;
    protected UserProfileDTO shownProfile;
    protected OwnedPortfolioIdList portfolioIdList;

    protected UserProfileRetrievedMilestone userProfileRetrievedMilestone;
    protected PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    private int displayingProfileHeaderLayoutId;
    private boolean cancelRefreshingOnResume;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_screen, container, false);
        userProfileView = (UserProfileView) inflater.inflate(R.layout.user_profile_view, null);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        this.actionBar = getSherlockActivity().getSupportActionBar();
        this.actionBar.setDisplayOptions(
                (isTabBarVisible() ? 0 : ActionBar.DISPLAY_HOME_AS_UP)
                        | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_settings:
                DashboardNavigator navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
                navigator.pushFragment(SettingsFragment.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void initViews(View view)
    {
        if (userProfileView != null)
        {
            userProfileView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    userProfileView.getChildAt(userProfileView.getDisplayedChild()).setVisibility(View.GONE);
                    userProfileView.showNext();
                    userProfileView.getChildAt(userProfileView.getDisplayedChild()).setVisibility(View.VISIBLE);
                }
            });
            userProfileView.setPortfolioRequestListener(this);
            timelineListView.getRefreshableView().addHeaderView(userProfileView);
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        UserBaseKey newUserBaseKey = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_SHOW_USER_ID));
        linkWith(newUserBaseKey, true);

        getActivity().getSupportLoaderManager()
                .initLoader(timelineAdapter.getLoaderId(), null, timelineAdapter.getLoaderCallback());
    }

    @Override public void onResume()
    {
        super.onResume();
        if (userProfileView != null && displayingProfileHeaderLayoutId != 0)
        {
            userProfileView.setDisplayedChildByLayoutId(displayingProfileHeaderLayoutId);
        }

        if (cancelRefreshingOnResume)
        {
            timelineListView.onRefreshComplete();
            cancelRefreshingOnResume = false;
        }
    }

    @Override public void onPause()
    {
        if (userProfileView != null)
        {
            displayingProfileHeaderLayoutId = userProfileView.getDisplayedChildLayoutId();
        }
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        this.actionBar = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        if (userProfileRetrievedMilestone != null)
        {
            userProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        userProfileRetrievedMilestone = null;

        this.timelineListView = null;
        this.timelineAdapter = null;

        if (userProfileView != null)
        {
            userProfileView.setPortfolioRequestListener(null);
        }
        this.userProfileView = null;
        super.onDestroyView();
    }

    //<editor-fold desc="Display methods">
    protected void linkWith(UserBaseKey userBaseKey, final boolean andDisplay)
    {
        this.shownUserBaseKey = userBaseKey;

        if (timelineAdapter == null)
        {
            timelineAdapter = createTimelineAdapter();
            timelineListView.setAdapter(timelineAdapter);
            timelineListView.setOnRefreshListener(timelineAdapter);
            timelineListView.setOnScrollListener(timelineAdapter);
            timelineListView.setOnLastItemVisibleListener(timelineAdapter);
            timelineListView.setRefreshing();
        }

        if (userBaseKey != null)
        {
            createUserProfileRetrievedMilestone();
            userProfileRetrievedMilestone.setOnCompleteListener(userProfileRetrievedMilestoneListener);
            userProfileRetrievedMilestone.launch();

            createPortfolioCompactListRetrievedMilestone();
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(portfolioCompactListRetrievedMilestoneListener);
            portfolioCompactListRetrievedMilestone.launch();
        }
    }

    protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.shownProfile = userProfileDTO;
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
        if (timelineScreen != null)
        {
            timelineScreen.setDisplayedChildByLayoutId(R.id.timeline_list_view);
        }
        if (userProfileView != null)
        {
            userProfileView.display(shownProfile);
        }
        if (this.actionBar != null)
        {
            this.actionBar.setTitle(UserBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
        }

        displayActionBarTitle();
    }
    //</editor-fold>

    protected void displayActionBarTitle()
    {
        if (actionBar != null)
        {
            if (shownProfile != null)
            {
                actionBar.setTitle(UserBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
            }
            else
            {
                actionBar.setTitle(R.string.loading_loading);
            }
        }
    }

    //<editor-fold desc="Init milestones">
    protected void createUserProfileRetrievedMilestone()
    {
        if (userProfileRetrievedMilestone != null)
        {
            userProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        userProfileRetrievedMilestone = new UserProfileRetrievedMilestone(shownUserBaseKey);
    }

    protected void createPortfolioCompactListRetrievedMilestone()
    {
        if (portfolioCompactListRetrievedMilestone != null)
        {
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(null);
        }
        portfolioCompactListRetrievedMilestone = new PortfolioCompactListRetrievedMilestone(shownUserBaseKey);
    }
    //</editor-fold>

    //<editor-fold desc="Initial methods">
    private TimelineAdapter createTimelineAdapter()
    {
        timelineAdapter = new TimelineAdapter(getActivity(), getActivity().getLayoutInflater(),
                shownUserBaseKey.key, R.layout.timeline_item_view);
        timelineAdapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<TimelineItem>()
        {

            @Override public void onLoadFinished(ListLoader<TimelineItem> loader, List<TimelineItem> data)
            {
                if (timelineListView != null)
                {
                    timelineListView.onRefreshComplete();
                    cancelRefreshingOnResume = true;
                }
            }

            @Override public ListLoader<TimelineItem> onCreateLoader(Bundle args)
            {
                return createTimelineLoader();
            }
        });
        return timelineAdapter;
    }

    private ListLoader<TimelineItem> createTimelineLoader()
    {
        TimelineListLoader timelineLoader = new TimelineListLoader(getActivity(), shownUserBaseKey);
        timelineLoader.setPerPage(Constants.TIMELINE_ITEM_PER_PAGE);
        return timelineLoader;
    }
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
            pushPositionListFragment(portfolioCompactListCache.get().getDefaultPortfolio(shownUserBaseKey));
        }
    }
    //</editor-fold>

    private void pushPositionListFragment(OwnedPortfolioId ownedPortfolioId)
    {
        Bundle args = new Bundle();
        args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE,
                ownedPortfolioId.getArgs());
        DashboardNavigator navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
        navigator.pushFragment(PositionListFragment.class, args);
    }

    //<editor-fold desc="Milestone retrieved listeners">
    private Milestone.OnCompleteListener userProfileRetrievedMilestoneListener =
            new Milestone.OnCompleteListener()
            {
                @Override public void onComplete(Milestone milestone)
                {
                    UserProfileDTO cachedUserProfile = userProfileCache.get().get(shownUserBaseKey);
                    if (cachedUserProfile != null)
                    {
                        linkWith(cachedUserProfile, true);
                    }
                }

                @Override public void onFailed(Milestone milestone, Throwable throwable)
                {
                    THToast.show(getString(R.string.error_fetch_user_profile));
                }
            };

    private Milestone.OnCompleteListener portfolioCompactListRetrievedMilestoneListener =
            new Milestone.OnCompleteListener()
            {
                @Override public void onComplete(Milestone milestone)
                {
                    OwnedPortfolioIdList cachedOwnedPortfolioIdList =
                            portfolioCompactListCache.get().get(shownUserBaseKey);
                    if (cachedOwnedPortfolioIdList != null)
                    {
                        linkWith(cachedOwnedPortfolioIdList, true);
                    }
                }

                @Override public void onFailed(Milestone milestone, Throwable throwable)
                {
                    // We do not need to inform the player here
                    Timber.e("Error fetching the list of portfolio for user: %d", shownUserBaseKey.key, throwable);
                }
            };

    //</editor-fold>

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
