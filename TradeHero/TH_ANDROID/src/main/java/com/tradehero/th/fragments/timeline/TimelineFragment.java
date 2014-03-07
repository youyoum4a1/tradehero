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
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListItemForProfileAdapter;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineFragment extends BasePurchaseManagerFragment
        implements PortfolioRequestListener
{
    public static final String BUNDLE_KEY_SHOW_USER_ID =
            TimelineFragment.class.getName() + ".showUserId";

    public static enum TabType
    {
        TIMELINE, PORTFOLIO_LIST, STATS
    }

    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    @InjectView(R.id.timeline_list_view) TimelineListView timelineListView;
    @InjectView(R.id.timeline_screen) BetterViewAnimator timelineScreen;

    private UserProfileView userProfileView;

    private MainTimelineAdapter timelineAdapter;

    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    private PortfolioListItemForProfileAdapter portfolioAdapter;
    private PortfolioListItemForProfileAdapter.PortfolioListRefreshRequestListener refreshRequestListener;

    protected ActionBar actionBar;

    protected UserBaseKey shownUserBaseKey;
    protected UserProfileDTO shownProfile;
    protected OwnedPortfolioIdList portfolioIdList;

    protected UserProfileRetrievedMilestone userProfileRetrievedMilestone;
    protected PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    private TimelineProfileClickListener profileButtonClickListener;

    private int displayingProfileHeaderLayoutId;
    private boolean cancelRefreshingOnResume;
    public TabType currentTab = TabType.TIMELINE;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        refreshRequestListener = new PortfolioListItemForProfileAdapter.PortfolioListRefreshRequestListener()
        {
            @Override public void onPortfolioRefreshRequested()
            {
                // TODO
            }
        };
        profileButtonClickListener = new TimelineProfileClickListener()
        {
            @Override public void onBtnClicked(TabType tabType)
            {
                linkWith(tabType, true);
            }
        };
    }

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

        portfolioAdapter = createPortfolioAdapter();
        portfolioAdapter.setProfileClickListener(profileButtonClickListener);

        displayablePortfolioFetchAssistant = new DisplayablePortfolioFetchAssistant();
        displayablePortfolioFetchAssistant.setFetchedListener(new DisplayablePortfolioFetchAssistant.OnFetchedListener()
        {
            @Override public void onFetched()
            {
                displayPortfolios();
            }
        });
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        UserBaseKey newUserBaseKey = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_SHOW_USER_ID));
        linkWith(newUserBaseKey, true);

        getActivity().getSupportLoaderManager()
                .initLoader(timelineAdapter.getLoaderId(), null, timelineAdapter.getLoaderTimelineCallback());
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
        displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());

        displayTab();
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
        detachTimelineAdapter();
        detachTimelineListView();
        detachPortfolioAdapter();

        displayablePortfolioFetchAssistant.setFetchedListener(null);
        displayablePortfolioFetchAssistant = null;

        if (userProfileRetrievedMilestone != null)
        {
            userProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        userProfileRetrievedMilestone = null;

        if (userProfileView != null)
        {
            userProfileView.setPortfolioRequestListener(null);
        }
        this.userProfileView = null;
        super.onDestroyView();
    }

    protected void detachTimelineAdapter()
    {
        if (timelineAdapter != null)
        {
            timelineAdapter.setProfileClickListener(null);
        }
        timelineAdapter = null;
    }

    protected void detachTimelineListView()
    {
        if (timelineListView != null)
        {
            timelineListView.setOnRefreshListener((TimelineAdapter) null);
            timelineListView.setOnScrollListener(null);
            timelineListView.setOnLastItemVisibleListener(null);
        }
        timelineListView = null;
    }

    protected void detachPortfolioAdapter()
    {
        if (portfolioAdapter != null)
        {
            portfolioAdapter.setProfileClickListener(null);
        }
        portfolioAdapter = null;
    }

    @Override public void onDestroy()
    {
        refreshRequestListener = null;
        profileButtonClickListener = null;
        super.onDestroy();
    }

    //<editor-fold desc="Display methods">
    protected void linkWith(UserBaseKey userBaseKey, final boolean andDisplay)
    {
        this.shownUserBaseKey = userBaseKey;

        prepareTimelineAdapter();

        if (userBaseKey != null)
        {
            createUserProfileRetrievedMilestone();
            userProfileRetrievedMilestone.setOnCompleteListener(userProfileRetrievedMilestoneListener);
            userProfileRetrievedMilestone.launch();

            createPortfolioCompactListRetrievedMilestone();
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(portfolioCompactListRetrievedMilestoneListener);
            portfolioCompactListRetrievedMilestone.launch();
        }

        if (andDisplay)
        {
            displayTab();
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

    protected void linkWith(TabType tabType, boolean andDisplay)
    {
        currentTab = tabType;
        if (andDisplay)
        {
            displayTab();
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

    public void displayTab()
    {
        switch (currentTab)
        {
            case TIMELINE:
                if (timelineListView.getAdapter() != timelineAdapter)
                {
                    timelineListView.setAdapter(timelineAdapter);
                }
                break;
            case PORTFOLIO_LIST:
                if (timelineListView.getAdapter() != portfolioAdapter)
                {
                    timelineListView.setAdapter(portfolioAdapter);
                }
                break;
            case STATS:
                // TODO
                break;
            default:
                throw new IllegalArgumentException("No TabType." + currentTab);
        }
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
    private MainTimelineAdapter createTimelineAdapter()
    {
        return new MainTimelineAdapter(getActivity(), getActivity().getLayoutInflater());//,
                        //shownUserBaseKey.key, R.layout.timeline_item_view);
        // TODO set the layouts
    }

    private void prepareTimelineAdapter()
    {
        timelineAdapter = createTimelineAdapter();
        timelineListView.setAdapter(timelineAdapter);
        timelineListView.setOnRefreshListener(timelineAdapter);
        timelineListView.setOnScrollListener(timelineAdapter);
        timelineListView.setOnLastItemVisibleListener(timelineAdapter);
        timelineListView.setRefreshing();
        timelineAdapter.setTimelineLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<TimelineItem>()
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
        timelineAdapter.setProfileClickListener(profileButtonClickListener);
    }

    private ListLoader<TimelineItem> createTimelineLoader()
    {
        TimelineListLoader timelineLoader = new TimelineListLoader(getActivity(), shownUserBaseKey);
        timelineLoader.setPerPage(Constants.TIMELINE_ITEM_PER_PAGE);
        return timelineLoader;
    }

    private PortfolioListItemForProfileAdapter createPortfolioAdapter()
    {
        return new PortfolioListItemForProfileAdapter(getActivity(), getActivity().getLayoutInflater(),
                R.layout.portfolio_list_item, R.layout.portfolio_list_header);
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

    protected List<UserBaseKey> getUserBaseKeys()
    {
        List<UserBaseKey> list = new ArrayList<>();
        list.add(currentUserId.toUserBaseKey());
        return list;
    }

    public void displayPortfolios()
    {
        if (portfolioAdapter != null)
        {
            portfolioAdapter.setItems(getAllPortfolios());
            portfolioAdapter.notifyDataSetChanged();
        }
        displayTab();
    }

    private List<DisplayablePortfolioDTO> getAllPortfolios()
    {
        if (displayablePortfolioFetchAssistant != null)
        {
            return displayablePortfolioFetchAssistant.getDisplayablePortfolios();
        }
        return null;
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
