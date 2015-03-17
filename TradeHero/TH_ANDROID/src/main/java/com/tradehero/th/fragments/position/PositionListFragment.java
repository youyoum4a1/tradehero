package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKeyFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Routable("user/:userId/portfolio/:portfolioId")
public class PositionListFragment
        extends DashboardFragment
        implements  WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = PositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = PositionListFragment.class.getName() + ".userBaseKey";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";

    @Inject CurrentUserId currentUserId;
    @Inject GetPositionsDTOKeyFactory getPositionsDTOKeyFactory;
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;
    @Inject UserProfileCache userProfileCache;

    @InjectView(R.id.position_list) protected ListView positionsListView;
    @InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.pull_to_refresh_position_list) PositionListView pullToRefreshListView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.error) View errorView;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    protected GetPositionsDTOKey getPositionsDTOKey;
    protected GetPositionsDTO getPositionsDTO;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    @Nullable protected PositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;

    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> refreshGetPositionsDTOListener;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Nullable protected DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchListener;
    @Inject THRouter thRouter;

    //<editor-fold desc="Arguments Handling">
    public static void putGetPositionsDTOKey(@NotNull Bundle args, @NotNull GetPositionsDTOKey getPositionsDTOKey)
    {
        args.putBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE, getPositionsDTOKey.getArgs());
    }

    private static GetPositionsDTOKey getGetPositionsDTOKey(@NotNull GetPositionsDTOKeyFactory getPositionsDTOKeyFactory, @NotNull Bundle args)
    {
        return getPositionsDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE));
    }

    public static void putShownUser(@NotNull Bundle args, @NotNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    @NotNull private static UserBaseKey getUserBaseKey(@NotNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE));
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        Bundle args = getArguments();
        if (args.containsKey(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE))
        {
            shownUser = getUserBaseKey(args);
        }
        else
        {
            shownUser = injectedUserBaseKey;
        }
        if (args.containsKey(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE))
        {
            getPositionsDTOKey = getGetPositionsDTOKey(getPositionsDTOKeyFactory, args);
        }
        else
        {
            getPositionsDTOKey = new OwnedPortfolioId(injectedUserBaseKey.key, injectedPortfolioId.key);
        }

        fetchGetPositionsDTOListener = createGetPositionsCacheListener();
        refreshGetPositionsDTOListener = createGetPositionsRefreshCacheListener();
        userProfileCacheListener = createProfileCacheListener();
        portfolioFetchListener = createPortfolioCacheListener();
    }


    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            firstPositionVisible = savedInstanceState.getInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        }

        View view = inflater.inflate(R.layout.fragment_positions_list, container, false);

        ButterKnife.inject(this, view);
        return view;
    }

    protected boolean checkLoadingSuccess()
    {
        return (userProfileDTO != null) && (getPositionsDTO != null);
    }

    protected void showResultIfNecessary()
    {
        boolean loaded = checkLoadingSuccess();
        Timber.d("checkLoadingSuccess %b", loaded);
        showLoadingView(!loaded);
        if (loaded && pullToRefreshListView != null)
        {
            pullToRefreshListView.onRefreshComplete();
        }
    }

    protected void showLoadingView(boolean shown)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setVisibility(shown ? View.GONE : View.VISIBLE);
        }
        if (errorView != null)
        {
            errorView.setVisibility(View.GONE);
        }
    }

    protected void showErrorView()
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setVisibility(View.GONE);
        }
        if (errorView != null)
        {
            errorView.setVisibility(View.VISIBLE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.onRefreshComplete();
        }
    }

    private void initPullToRefreshListView(View view)
    {
        //TODO make it better
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                refreshSimplePage();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
            }
        });
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                handlePositionItemClicked(adapterView, view, i, l);
            }
        });
    }

    protected void createPositionItemAdapter()
    {
        positionItemAdapter = new PositionItemAdapter(
                getActivity(),
                getLayoutResIds());
    }

    @NotNull protected Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_IN_PERIOD, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED_IN_PERIOD, R.layout.position_top_view);
        return layouts;
    }

    protected void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
    }

    @Override public void onResume()
    {
        super.onResume();

        linkWith(getPositionsDTOKey, true);
    }

    @Override public void onPause()
    {

        if (positionsListView != null)
        {
            firstPositionVisible = positionsListView.getFirstVisiblePosition();
        }
        super.onPause();
    }

    @Override public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        detachGetPositionsTask();
        detachUserProfileCache();
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
    }

    @Override public void onStop()
    {
        detachGetPositionsTask();
        detachRefreshGetPositionsTask();
        detachUserProfileCache();
        detachPortfolioCache();

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (positionsListView != null)
        {
            positionsListView.setOnScrollListener(null);
            positionsListView.setOnTouchListener(null);
        }
        positionItemAdapter = null;

        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setOnRefreshListener((PullToRefreshBase.OnRefreshListener<ListView>) null);
        }

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        fetchGetPositionsDTOListener = null;
        refreshGetPositionsDTOListener = null;
        userProfileCacheListener = null;
        portfolioFetchListener = null;
        super.onDestroy();
    }

    /**
     * start
     * @param positionsDTOKey
     * @param andDisplay
     */
    public void linkWith(GetPositionsDTOKey positionsDTOKey, boolean andDisplay)
    {
        this.getPositionsDTOKey = positionsDTOKey;
        userProfileDTO = null;

        fetchUserProfile();
        fetchSimplePage();
        fetchPortfolio();
        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(shownUser, userProfileCacheListener);
        userProfileCache.getOrFetchAsync(shownUser);
    }

    public boolean isShownOwnedPortfolioIdForOtherPeople(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        return ownedPortfolioId != null && ownedPortfolioId.portfolioId <= 0;
    }

    protected void fetchSimplePage()
    {
        fetchSimplePage(false);
    }

    protected void fetchSimplePage(boolean force)
    {
        if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
        {
            detachGetPositionsTask();
            getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
            getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, force);
        }
    }

    protected void refreshSimplePage()
    {
        detachRefreshGetPositionsTask();
        getPositionsCache.get().register(getPositionsDTOKey, refreshGetPositionsDTOListener);
        getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, true);
    }

    protected void fetchPortfolio()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            if (currentUserId.toUserBaseKey().equals(((OwnedPortfolioId) getPositionsDTOKey).getUserBaseKey()))
            {
                PortfolioCompactDTO cached = portfolioCompactCache.get(((OwnedPortfolioId) getPositionsDTOKey).getPortfolioIdKey());
                if (cached == null)
                {
                    detachPortfolioCache();
                    portfolioCache.register((OwnedPortfolioId) getPositionsDTOKey, portfolioFetchListener);
                    portfolioCache.get((OwnedPortfolioId) getPositionsDTOKey);
                }
            }
            // We do not need to fetch for other players
        }
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void detachGetPositionsTask()
    {
        getPositionsCache.get().unregister(fetchGetPositionsDTOListener);
    }

    protected void detachRefreshGetPositionsTask()
    {
        getPositionsCache.get().unregister(refreshGetPositionsDTOListener);
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void detachPortfolioCache()
    {
        portfolioCache.unregister(portfolioFetchListener);
    }

    protected void rePurposeAdapter()
    {
        if (this.getPositionsDTO != null)
        {
            createPositionItemAdapter();
            positionItemAdapter.addAll(getPositionsDTO.positions);
            positionItemAdapter.notifyDataSetChanged();
            pullToRefreshListView.setAdapter(positionItemAdapter);
            //if (positionsListView != null)
            //{
            //    positionsListView.setAdapter(positionItemAdapter);
            //}
        }
    }

    public void linkWith(GetPositionsDTO getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        //reAttemptFetchPortfolio();
        rePurposeAdapter();

        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            displayHeaderView();
        }
    }

    public void display()
    {
        displayHeaderView();
        displayActionBarTitle();
    }

    private void displayHeaderView()
    {
    }

    public void displayActionBarTitle()
    {
        if (getPositionsDTO != null && getPositionsDTO.positions != null)
        {
            String title = String.format(getResources().getString(R.string.position_list_action_bar_header),
                    getPositionsDTO.positions.size());
            setActionBarTitle(title);
        }
        else
        {
            setActionBarTitle(R.string.position_list_action_bar_header_unknown);
        }
    }
    //</editor-fold>

    protected void popFollowUser(final UserBaseKey userBaseKey) {}

        @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener ()
        {
            return null;
        }

        protected class GetPositionsListener
                implements DTOCacheNew.HurriedListener<GetPositionsDTOKey, GetPositionsDTO> {
            @Override
            public void onPreCachedDTOReceived(
                    @NotNull GetPositionsDTOKey key,
                    @NotNull GetPositionsDTO value) {
                linkWith(value, true);
                showResultIfNecessary();
            }

            @Override
            public void onDTOReceived(
                    @NotNull GetPositionsDTOKey key,
                    @NotNull GetPositionsDTO value) {
                linkWith(value, true);
                showResultIfNecessary();
            }

            @Override
            public void onErrorThrown(
                    @NotNull GetPositionsDTOKey key,
                    @NotNull Throwable error) {
                //displayProgress(false);
                THToast.show(getString(R.string.error_fetch_position_list_info));
                showErrorView();
                Timber.d(error, "Error fetching the positionList info %s", key);
            }
        }

        @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsRefreshCacheListener
        ()
        {
            return null;
        }


    public int getTutorialLayout() {
        return R.layout.tutorial_position_list;
    }

    @NotNull
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createProfileCacheListener() {
        return new AbstractPositionListProfileCacheListener();
    }

    protected class AbstractPositionListPremiumUserFollowedListener
            implements PremiumFollowUserAssistant.OnUserFollowedListener {
        @Override
        public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO) {
            displayHeaderView();
            fetchSimplePage(true);
            //analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.PositionList));
        }

        @Override
        public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error) {
            // do nothing for now
        }
    }

    protected class AbstractPositionListProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> {
        @Override
        public void onDTOReceived(
                @NotNull UserBaseKey key,
                @NotNull UserProfileDTO value) {
            linkWith(value, true);
            showResultIfNecessary();
        }

        @Override
        public void onErrorThrown(
                @NotNull UserBaseKey key,
                @NotNull Throwable error) {
            THToast.show(R.string.error_fetch_user_profile);
            //TODO not just toast
            showErrorView();
        }
    }

    protected DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioCacheListener() {
        return new PortfolioCacheListener();
    }

    protected class PortfolioCacheListener implements DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> {
        @Override
        public void onDTOReceived(@NotNull OwnedPortfolioId key, @NotNull PortfolioDTO value) {

        }

        @Override
        public void onErrorThrown(@NotNull OwnedPortfolioId key, @NotNull Throwable error) {
            THToast.show(R.string.error_fetch_portfolio_info);
        }
    }

}
