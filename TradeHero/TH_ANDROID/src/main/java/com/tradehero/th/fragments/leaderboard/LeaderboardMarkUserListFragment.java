package com.tradehero.th.fragments.leaderboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.PerPagedFilteredLeaderboardKeyPreference;
import com.tradehero.th.persistence.leaderboard.PerPagedLeaderboardKeyPreference;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class LeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    public static final String PREFERENCE_KEY_PREFIX = LeaderboardMarkUserListFragment.class.getName();

    @Inject Analytics analytics;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject @ForUser SharedPreferences preferences;
    @Inject Lazy<HeroAlertDialogUtil> heroAlertDialogUtilLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;
    @Inject protected Lazy<HeroListCache> heroListCacheLazy;

    @InjectView(R.id.leaderboard_mark_user_listview) LeaderboardMarkUserListView leaderboardMarkUserListView;
    @InjectView(R.id.leaderboard_mark_user_screen) BetterViewAnimator leaderboardMarkUserScreen;

    private TextView leaderboardMarkUserMarkingTime;

    protected LeaderboardMarkUserLoader leaderboardMarkUserLoader;
    protected LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;

    protected LeaderboardFilterFragment leaderboardFilterFragment;

    protected PerPagedLeaderboardKeyPreference savedPreference;
    protected PerPagedLeaderboardKey currentLeaderboardKey;

    protected FollowDialogCombo followDialogCombo;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        currentLeaderboardKey = getInitialLeaderboardKey();
    }

    protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        savedPreference = new PerPagedFilteredLeaderboardKeyPreference(
                getActivity(),
                preferences,
                PREFERENCE_KEY_PREFIX + leaderboardDefKey,
                LeaderboardFilterSliderContainer.getStartingFilter(getResources(), leaderboardDefKey.key).getFilterStringSet());
        PerPagedFilteredLeaderboardKey initialKey = ((PerPagedFilteredLeaderboardKeyPreference) savedPreference)
                .getPerPagedFilteredLeaderboardKey();
        // We override here to make sure we do not pick up key, page or perPage from the preference
        return new PerPagedFilteredLeaderboardKey(initialKey, leaderboardDefKey.key, null, null);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_mark_user_listview, container, false);
        initViews(view);
        inflateHeaderView(inflater, container);

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setEmptyView(inflateEmptyView(inflater, container));
        }
        return view;
    }

    protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
    {
        /*
         TODO I haven't seen a basic leaderboard empty view except the one from Friend leaderboard
         on iOS app, therefore, I will use a dummy empty view here
         */
        return inflater.inflate(R.layout.leaderboard_empty_view, container, false);
    }

    protected void inflateHeaderView(
            @NotNull LayoutInflater inflater,
            @SuppressWarnings("UnusedParameters") ViewGroup container)
    {
        if (leaderboardMarkUserListView != null)
        {
            View headerView = inflater.inflate(getHeaderViewResId(), null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView, null, false);
                initHeaderView(headerView);
            }
            View rankHeaderView = inflateAndGetUserRankHeaderView();
            if (rankHeaderView != null)
            {
                leaderboardMarkUserListView.getRefreshableView().addHeaderView(rankHeaderView);
            }
        }
    }

    protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header;
    }

    protected void initHeaderView(View headerView)
    {
        String leaderboardDefDesc = getArguments().getString(BUNDLE_KEY_LEADERBOARD_DEF_DESC);

        TextView leaderboardMarkUserTimePeriod = (TextView) headerView.findViewById(R.id.leaderboard_time_period);
        if (leaderboardMarkUserTimePeriod != null)
        {
            if (leaderboardDefDesc != null)
            {
                leaderboardMarkUserTimePeriod.setText(leaderboardDefDesc);
                leaderboardMarkUserTimePeriod.setVisibility(View.VISIBLE);
            }
            else
            {
                leaderboardMarkUserTimePeriod.setVisibility(View.GONE);
            }
        }
        leaderboardMarkUserMarkingTime = (TextView) headerView.findViewById(R.id.leaderboard_marking_time);
    }

    //<editor-fold desc="ActionBar">
    @Override protected int getMenuResource()
    {
        return R.menu.leaderboard_listview_menu;
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        displayFilterIcon(menu.findItem(R.id.leaderboard_listview_menu_help));
        super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Back));
                break;
            case R.id.button_leaderboard_filter:
                pushFilterFragmentIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        prepareLeaderboardMarkUserAdapter();
    }

    protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardDefKey.key, R.layout.lbmu_item_roi_mode);
    }

    protected void prepareLeaderboardMarkUserAdapter()
    {
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setFollowRequestedListener(null);
        }
        leaderboardMarkUserListAdapter = createLeaderboardMarkUserAdapter();
        leaderboardMarkUserListAdapter.setDTOLoaderCallback(new LeaderboardMarkUserListViewFragmentListLoaderCallback());
        leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        leaderboardMarkUserListAdapter.setApplicablePortfolioId(getApplicablePortfolioId());
        leaderboardMarkUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
        leaderboardMarkUserListView.setOnRefreshListener(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);

        Bundle loaderBundle = new Bundle(getArguments());
        leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getActivity().getSupportLoaderManager().initLoader(
                leaderboardDefKey.key, loaderBundle, leaderboardMarkUserListAdapter.getLoaderCallback());
    }

    @Override public void onResume()
    {
        super.onResume();
        if (leaderboardFilterFragment != null)
        {
            PerPagedFilteredLeaderboardKey newLeaderboardKey = leaderboardFilterFragment.getPerPagedFilteredLeaderboardKey();
            leaderboardFilterFragment = null;
            Timber.d("%s", newLeaderboardKey.equals(currentLeaderboardKey));

            if (!newLeaderboardKey.equals(currentLeaderboardKey))
            {
                currentLeaderboardKey = newLeaderboardKey;
                leaderboardMarkUserListView.setRefreshing();
                initialLoad();
            }
            getActivity().invalidateOptionsMenu();
        }
        else
        {
            Timber.d("onResume filterFragment is null");
        }
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (leaderboardMarkUserListAdapter != null && purchaseApplicablePortfolioId != null)
        {
            leaderboardMarkUserListAdapter.setApplicablePortfolioId(purchaseApplicablePortfolioId);
        }
    }

    @Override public void onStop()
    {
        detachFollowDialogCombo();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setDTOLoaderCallback(null);
            leaderboardMarkUserListAdapter.setFollowRequestedListener(null);
        }
        leaderboardMarkUserListAdapter = null;

        leaderboardMarkUserListView.setOnRefreshListener((LeaderboardMarkUserListAdapter) null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.leaderboardFilterFragment = null;
        saveCurrentFilterKey();
        getActivity().getSupportLoaderManager().destroyLoader(leaderboardDefKey.key);
        super.onDestroy();
    }

    protected void saveCurrentFilterKey()
    {
        savedPreference.set(currentLeaderboardKey);
    }

    protected void detachFollowDialogCombo()
    {
        FollowDialogCombo followDialogComboCopy = followDialogCombo;
        if (followDialogComboCopy != null)
        {
            followDialogComboCopy.followDialogView.setFollowRequestedListener(null);
        }
        followDialogCombo = null;
    }

    protected void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    @Override protected void setCurrentUserProfileDTO(UserProfileDTO userProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if(leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        }
        if (userProfileCache != null && currentUserId != null)
        {
            UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
            userProfileCache.put(userBaseKey, userProfileDTO);
        }
    }

    public void initialLoad()
    {
        Timber.d("initialLoad %s", currentLeaderboardKey);
        leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
        leaderboardMarkUserLoader.reload();
        //invalidateCachedItemView();
    }

    private void updateListViewRow(UserBaseKey userBaseKey)
    {
        AdapterView list = leaderboardMarkUserListView.getRefreshableView();
        int start = list.getFirstVisiblePosition();
        for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
        {
            Object target = list.getItemAtPosition(i);
            if (target instanceof UserBaseDTO)
            {
                UserBaseDTO user = (UserBaseDTO) target;
                if (user.getBaseKey().equals(userBaseKey))
                {

                    View view = list.getChildAt(i - start);
                    list.getAdapter().getView(i, view, list);
                    break;
                }
            }
        }
    }

    /**
     * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.0.3_r1/android/widget/ListView.java#443
     * this crazy way works and is the only way I found to clear ListView's recycle (reusing item view)
     */

    /**
     * Update 22 Feb 2014: We are not using different mode for leaderboard item type anymore, Instead, filter mode feature is implemented, therefore,
     * no need to clear listview's recycle!!!
     */
    @Deprecated
    protected void invalidateCachedItemView()
    {
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
    }

    protected void pushFilterFragmentIn()
    {
        Bundle args = new Bundle();
        LeaderboardFilterFragment.putPerPagedFilteredLeaderboardKey(args, (PerPagedFilteredLeaderboardKey) currentLeaderboardKey);
        this.leaderboardFilterFragment = getDashboardNavigator().pushFragment(LeaderboardFilterFragment.class, args);
    }

    protected void displayFilterIcon(MenuItem filterIcon)
    {
        if (filterIcon != null)
        {
            if (currentLeaderboardKey instanceof PerPagedFilteredLeaderboardKey)
            {
                boolean areEqual = LeaderboardFilterSliderContainer.areInnerValuesEqualToStarting(
                        getResources(),
                        (PerPagedFilteredLeaderboardKey) currentLeaderboardKey);
                filterIcon.setIcon(
                        areEqual ?
                                R.drawable.ic_action_icn_actionbar_filteroff :
                                R.drawable.ic_action_icn_actionbar_filteron
                );
            }
            else
            {
                filterIcon.setIcon(R.drawable.ic_action_icn_actionbar_filteroff);
            }
        }
    }

//<<<<<<< HEAD
//    protected void handleFollowRequested(final UserBaseKey userBaseKey)
//    {
//        //TODO hacked by alipay alex
//        //heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
//        //{
//        //    @Override public void onClick(DialogInterface dialog, int which)
//        //    {
//                premiumFollowUser(userBaseKey);
//        //    }
//        //});
//    }
//
//    protected void handleFollowSuccess(UserProfileDTO userProfileDTO)
//    {
//        setCurrentUserProfileDTO(userProfileDTO);
//    }
//
//=======
//>>>>>>> origin/develop2.0
    protected class LeaderboardMarkUserListViewFragmentListLoaderCallback extends LoaderDTOAdapter.ListLoaderCallback<LeaderboardUserDTO>
    {
        @Override public ListLoader<LeaderboardUserDTO> onCreateLoader(Bundle args)
        {
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = new LeaderboardMarkUserLoader(getActivity(), currentLeaderboardKey);
            leaderboardMarkUserLoader.setPerPage(Constants.LEADERBOARD_MARK_USER_ITEM_PER_PAGE);
            return leaderboardMarkUserLoader;
        }

        @Override public void onLoadFinished(ListLoader<LeaderboardUserDTO> loader, List<LeaderboardUserDTO> data)
        {
            // display marking time
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) loader;
            Date markingTime = leaderboardMarkUserLoader.getMarkUtc();
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(String.format("(%s)", prettyTime.get().format(markingTime)));
            }
            leaderboardMarkUserScreen.setDisplayedChildByLayoutId(R.id.leaderboard_mark_user_listview);
            leaderboardMarkUserListView.onRefreshComplete();
        }
    }

    protected class LeaderboardMarkUserListFollowRequestedListener implements LeaderboardMarkUserItemView.OnFollowRequestedListener
    {
        @Override public void onFollowRequested(UserBaseDTO userBaseDTO)
        {
            handleFollowRequested(userBaseDTO);
        }
    }

    protected void handleFollowRequested(@NotNull final UserBaseDTO userBaseDTO)
    {
        detachFollowDialogCombo();
        followDialogCombo = heroAlertDialogUtilLazy.get().showFollowDialog(getActivity(), userBaseDTO,
                UserProfileDTOUtil.IS_NOT_FOLLOWER,
                createFollowRequestedListener());
    }

    protected OnFollowRequestedListener createFollowRequestedListener()
    {
        return new LeaderBoardFollowRequestedListener();
    }

    protected class LeaderBoardFollowRequestedListener
            implements OnFollowRequestedListener
    {
        @Override public void freeFollowRequested(@NotNull UserBaseKey heroId)
        {
            freeFollow(heroId);
        }

        @Override public void premiumFollowRequested(@NotNull UserBaseKey heroId)
        {
            premiumFollowUser(heroId);
        }
    }

    protected void freeFollow(@NotNull UserBaseKey heroId)
    {
        heroAlertDialogUtilLazy.get().showProgressDialog(
                getActivity(),
                getString(R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleCallback =
                userServiceWrapperLazy.get()
                        .freeFollow(heroId, new FreeFollowCallback(heroId));
    }

    public class FreeFollowCallback implements retrofit.Callback<UserProfileDTO>
    {
        private final UserBaseKey heroId;

        public FreeFollowCallback(UserBaseKey heroId)
        {
            this.heroId = heroId;
        }

        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            heroAlertDialogUtilLazy.get().dismissProgressDialog();
            setCurrentUserProfileDTO(userProfileDTO);
            heroListCacheLazy.get().invalidate(userProfileDTO.getBaseKey());
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));

            updateListViewRow(heroId);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            heroAlertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new LeaderboardMarkUserListPremiumUserFollowedListener();
    }

    protected class LeaderboardMarkUserListPremiumUserFollowedListener implements PremiumFollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
        {
            setCurrentUserProfileDTO(currentUserProfileDTO);
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Leaderboard));
        }

        @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
        {
            // nothing for now
        }
    }
}
