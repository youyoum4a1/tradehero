package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.android.internal.util.Predicate;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import dagger.Lazy;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.observables.AndroidObservable;

public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    @Nullable @Optional @InjectView(R.id.leaderboard_mark_user_listview) ListView leaderboardMarkUserListView;
    @InjectView(R.id.progress) ProgressBar mProgress;
    @Nullable protected View headerView;

    protected LeaderboardFriendsSetAdapter leaderboardFriendsUserListAdapter;
    private TextView leaderboardMarkUserMarkingTime;
    @Inject Analytics analytics;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @Inject Lazy<AdapterViewUtils> adapterViewUtilsLazy;

    protected FollowDialogCombo followDialogCombo;
    protected ChoiceFollowUserAssistantWithDialog choiceFollowUserAssistantWithDialog;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardFriendsUserListAdapter = new LeaderboardFriendsSetAdapter(
                getActivity(),
                R.layout.lbmu_item_roi_mode,
                R.layout.leaderboard_friends_social_item_view);
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_friends_listview, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        inflateHeaderView(inflater, container);

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setEmptyView(inflateEmptyView(inflater, container));
            leaderboardMarkUserListView.setAdapter(leaderboardFriendsUserListAdapter);
            leaderboardMarkUserListView.setOnItemClickListener(singleExpandingListViewListener);
            leaderboardMarkUserListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        }
        leaderboardFriendsUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
        return view;
    }

    @Override protected void initViews(View view)
    {
    }

    protected void inflateHeaderView(@NonNull LayoutInflater inflater, ViewGroup container)
    {
        if (leaderboardMarkUserListView != null)
        {
            headerView = inflater.inflate(R.layout.leaderboard_listview_header, null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.addHeaderView(headerView, null, false);
                initHeaderView();
            }
        }
    }

    protected void initHeaderView()
    {
        String leaderboardDefDesc = leaderboardDefDTO == null ? null : leaderboardDefDTO.desc;
        TextView leaderboardMarkUserTimePeriod =
                (TextView) headerView.findViewById(R.id.leaderboard_time_period);
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
        leaderboardMarkUserMarkingTime =
                (TextView) headerView.findViewById(R.id.leaderboard_marking_time);
    }

    @Override public void onStart()
    {
        super.onStart();
        leaderboardFriendsUserListAdapter.clear();
        leaderboardFriendsUserListAdapter.notifyDataSetChanged();
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.FriendsLeaderboard_Filter_FoF));
        fetchLeaderboardFriends();
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override protected int getMenuResource()
    {
        return R.menu.friend_leaderboard_menu;
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.friend_leaderboard_invite:
                pushInvitationFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        detachFollowDialogCombo();
        detachChoiceFollowAssistant();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        leaderboardFriendsUserListAdapter.setFollowRequestedListener(null);
        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setOnItemClickListener(null);
            leaderboardMarkUserListView.setOnScrollListener(null);
        }

        headerView = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardFriendsUserListAdapter.clear();
        leaderboardFriendsUserListAdapter = null;
        super.onDestroy();
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

    protected void detachChoiceFollowAssistant()
    {
        ChoiceFollowUserAssistantWithDialog copy = choiceFollowUserAssistantWithDialog;
        if (copy != null)
        {
            copy.onDestroy();
        }
        choiceFollowUserAssistantWithDialog = null;
    }

    protected View inflateEmptyView(@NonNull LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
    }

    private void pushInvitationFragment()
    {
        navigator.get().pushFragment(FriendsInvitationFragment.class);
    }

    @Override protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if (leaderboardFriendsUserListAdapter != null)
        {
            leaderboardFriendsUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
            leaderboardFriendsUserListAdapter.notifyDataSetChanged();
        }
    }

    private void fetchLeaderboardFriends()
    {
        LeaderboardFriendsKey key = new LeaderboardFriendsKey();
        AndroidObservable.bindFragment(this,
                leaderboardFriendsCache.get(key))
                .subscribe(createFriendsObserver());
    }

    private void handleFriendsLeaderboardReceived(@NonNull LeaderboardFriendsDTO dto)
    {
        Date markingTime = dto.leaderboard.markUtc;
        if (markingTime != null && leaderboardMarkUserMarkingTime != null)
        {
            leaderboardMarkUserMarkingTime.setText(
                    String.format("(%s)", prettyTime.get().format(markingTime)));
        }
        leaderboardFriendsUserListAdapter.add(dto);
    }

    protected class LeaderboardMarkUserListFollowRequestedListener
            implements LeaderboardMarkUserItemView.OnFollowRequestedListener
    {
        @Override public void onFollowRequested(UserBaseDTO userBaseDTO)
        {
            handleFollowRequested(userBaseDTO);
        }
    }

    @NonNull @Override protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new LeaderboardMarkUserListPremiumUserFollowedListener();
    }

    protected class LeaderboardMarkUserListPremiumUserFollowedListener implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(@NonNull UserBaseKey userFollowed, @NonNull UserProfileDTO currentUserProfileDTO)
        {
            handleFollowSuccess(currentUserProfileDTO);
        }

        @Override public void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error)
        {
            // nothing for now
        }
    }

    protected void handleFollowRequested(@NonNull final UserBaseDTO userBaseDTO)
    {
        detachChoiceFollowAssistant();
        choiceFollowUserAssistantWithDialog = new ChoiceFollowUserAssistantWithDialog(
                getActivity(),
                userBaseDTO.getBaseKey(),
                createUserFollowedListener(),
                getApplicablePortfolioId());
        choiceFollowUserAssistantWithDialog.setHeroBaseInfo(userBaseDTO);
        choiceFollowUserAssistantWithDialog.launchChoice();
    }

    @NonNull protected SimpleFollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new LeaderboardMarkUserListOnUserFollowedListener();
    }

    protected class LeaderboardMarkUserListOnUserFollowedListener implements SimpleFollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(@NonNull UserBaseKey userFollowed, @NonNull UserProfileDTO currentUserProfileDTO)
        {
            setCurrentUserProfileDTO(currentUserProfileDTO);
            int followType = currentUserProfileDTO.getFollowType(userFollowed);
            if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
            {
                analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
            }
            else if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
            {
                analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Leaderboard));
            }
            updateListViewRow(userFollowed);
        }

        @Override public void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void updateListViewRow(final UserBaseKey heroId)
    {
        AdapterView list = leaderboardMarkUserListView;
        adapterViewUtilsLazy.get().updateSingleRowWhere(list, FriendLeaderboardMarkedUserDTO.class, new Predicate<FriendLeaderboardMarkedUserDTO>()
        {
            @Override public boolean apply(FriendLeaderboardMarkedUserDTO friendLeaderboardMarkedUserDTO)
            {
                return friendLeaderboardMarkedUserDTO.leaderboardUserDTO.getBaseKey().equals(heroId);
            }
        });
    }

    protected void handleFollowSuccess(@NonNull UserProfileDTO userProfileDTO)
    {
        setCurrentUserProfileDTO(userProfileDTO);
    }

    @NonNull protected Observer<Pair<LeaderboardFriendsKey, LeaderboardFriendsDTO>> createFriendsObserver()
    {
        return new FriendLeaderboarMarkUserListFragmentObserver();
    }

    protected class FriendLeaderboarMarkUserListFragmentObserver implements Observer<Pair<LeaderboardFriendsKey, LeaderboardFriendsDTO>>
    {
        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            mProgress.setVisibility(View.INVISIBLE);
            if (e instanceof RetrofitError)
            {
                THToast.show(new THException(e));
            }
        }

        @Override public void onNext(Pair<LeaderboardFriendsKey, LeaderboardFriendsDTO> pair)
        {
            mProgress.setVisibility(View.INVISIBLE);
            handleFriendsLeaderboardReceived(pair.second);
        }
    }
}