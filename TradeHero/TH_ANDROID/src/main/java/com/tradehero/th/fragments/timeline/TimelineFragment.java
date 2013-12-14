package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.widget.StepView;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class TimelineFragment extends BasePurchaseManagerFragment
        implements StepView.StepProvider, PortfolioRequestListener
{
    public static final String TAG = TimelineFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_SHOW_USER_ID =
            TimelineFragment.class.getName() + ".showUserId";

    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    private TimelineAdapter timelineAdapter;
    private TimelineListView timelineListView;
    private StepView stepView;

    protected UserBaseKey shownUserBaseKey;
    protected UserProfileDTO shownProfile;
    protected OwnedPortfolioIdList portfolioIdList;

    private UserProfileRetrievedMilestone userProfileRetrievedMilestone;
    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_screen, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        timelineListView = (TimelineListView) view.findViewById(R.id.pull_refresh_list);
        timelineListView.setEmptyView(view.findViewById(android.R.id.empty));
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu, menu);

        getSherlockActivity().getSupportActionBar().setDisplayOptions(
                (isTabBarVisible() ? 0 : ActionBar.DISPLAY_HOME_AS_UP)
                        | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        if (shownProfile != null)
        {
            getSherlockActivity().getSupportActionBar().setTitle(shownProfile.displayName);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_settings:
                Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                navigator.openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        UserBaseKey newUserBaseKey =
                new UserBaseKey(getArguments().getInt(BUNDLE_KEY_SHOW_USER_ID));
        linkWith(newUserBaseKey, true);

        getActivity().getSupportLoaderManager()
                .initLoader(timelineAdapter.getLoaderId(), null, timelineAdapter.getLoaderCallback());
    }

    //<editor-fold desc="Display methods">
    protected void linkWith(UserBaseKey userBaseKey, final boolean andDisplay)
    {
        this.shownUserBaseKey = userBaseKey;
        if (timelineAdapter == null)
        {
            timelineAdapter = createTimelineAdapter();
        }

        timelineListView.setAdapter(timelineAdapter);
        timelineListView.setOnRefreshListener(timelineAdapter);
        timelineListView.setOnScrollListener(timelineAdapter);
        timelineListView.setOnLastItemVisibleListener(timelineAdapter);

        if (userBaseKey != null)
        {
            getUserProfileRetrievedMilestone().setOnCompleteListener(userProfileRetrievedMilestoneListener);
            getUserProfileRetrievedMilestone().launch();

            getPortfolioCompactListRetrievedMilestone().setOnCompleteListener(portfolioCompactListRetrievedMilestoneListener);
            getPortfolioCompactListRetrievedMilestone().launch();
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
        // TODO retain state for stepView
        stepView = new StepView(getActivity(), getActivity().getLayoutInflater());
        stepView.setStepProvider(this);

        if (timelineListView.getRefreshableView().getHeaderViewsCount() == 1)
        {
            timelineListView.addHeaderView(stepView);
        }

        if (shownProfile != null)
        {
            getSherlockActivity().getSupportActionBar().setTitle(shownProfile.displayName);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Init milestones">
    protected Milestone getPortfolioCompactListRetrievedMilestone()
    {
        if (portfolioCompactListRetrievedMilestone == null)
        {
            portfolioCompactListRetrievedMilestone =
                    new PortfolioCompactListRetrievedMilestone(shownUserBaseKey);
        }
        return portfolioCompactListRetrievedMilestone;
    }

    protected Milestone getUserProfileRetrievedMilestone()
    {
        if (userProfileRetrievedMilestone == null)
        {
            userProfileRetrievedMilestone = new UserProfileRetrievedMilestone(shownUserBaseKey);
        }
        return userProfileRetrievedMilestone;
    }

    //</editor-fold>

    //<editor-fold desc="Initial methods">
    private TimelineAdapter createTimelineAdapter()
    {
        timelineAdapter = new TimelineAdapter(getActivity(), getActivity().getLayoutInflater(),
                shownUserBaseKey.key, R.layout.timeline_item_view);
        timelineAdapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<TimelineItem>()
        {

            @Override
            public void onLoadFinished(ListLoader<TimelineItem> loader, List<TimelineItem> data)
            {
                timelineListView.onRefreshComplete();
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

    @Override public View provideView(int step)
    {
        switch (step)
        {
            case 0:
                ProfileCompactView profileCompactView =
                        (ProfileCompactView) getActivity().getLayoutInflater()
                                .inflate(R.layout.profile_screen_user_compact, null);
                profileCompactView.display(shownProfile);
                profileCompactView.setPortfolioRequestListener(this);
                return profileCompactView;
            case 1:
                ProfileView profileView = (ProfileView) getActivity().getLayoutInflater()
                        .inflate(R.layout.profile_screen_user_detail, null);
                profileView.display(shownProfile);
                profileView.setPortfolioRequestListener(this);
                return profileView;
        }
        return null;
    }

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
        Bundle args = new Bundle();
        args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE,
                ownedPortfolioId.getArgs());
        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
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
                    THLog.e(TAG, "Error fetching the list of portfolio for user: "
                            + shownUserBaseKey.key, throwable);
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
