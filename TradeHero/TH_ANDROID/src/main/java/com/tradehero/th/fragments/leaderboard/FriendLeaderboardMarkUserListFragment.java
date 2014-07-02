package com.tradehero.th.fragments.leaderboard;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.RetrofitError;

public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    @InjectView(R.id.leaderboard_mark_user_listview) ListView leaderboardMarkUserListView;
    @InjectView(R.id.progress) ProgressBar mProgress;

    protected LeaderboardFriendsListAdapter leaderboardFriendsUserListAdapter;
    private TextView leaderboardMarkUserMarkingTime;
    private DTOCacheNew.Listener<LeaderboardFriendsKey, LeaderboardFriendsDTO> leaderboardFriendsKeyDTOListener;
    @Inject THLocalyticsSession localyticsSession;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardFriendsCache leaderboardFriendsCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardFriendsKeyDTOListener = this.createFriendsCacheListener();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (leaderboardFriendsUserListAdapter == null)
        {
            leaderboardFriendsUserListAdapter = new LeaderboardFriendsListAdapter(
                    getActivity(),
                    R.layout.lbmu_item_roi_mode,
                    R.layout.leaderboard_friends_social_item_view);
            leaderboardFriendsUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
            leaderboardMarkUserListView.setAdapter(leaderboardFriendsUserListAdapter);
            leaderboardMarkUserListView.setOnItemClickListener(singleExpandingListViewListener);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_friends_listview, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        inflateHeaderView(inflater, container);

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setEmptyView(inflateEmptyView(inflater, container));
        }
        return view;
    }

    @Override protected void initViews(View view)
    {
    }

    protected void inflateHeaderView(LayoutInflater inflater, ViewGroup container)
    {
        if (leaderboardMarkUserListView != null)
        {
            View headerView = inflater.inflate(R.layout.leaderboard_listview_header, null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.addHeaderView(headerView, null, false);
                initHeaderView(headerView);
            }
        }
    }

    protected void initHeaderView(View headerView)
    {
        String leaderboardDefDesc = getArguments().getString(BUNDLE_KEY_LEADERBOARD_DEF_DESC);
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

    @Override public void onResume()
    {
        super.onResume();
        localyticsSession.tagEvent(LocalyticsConstants.FriendsLeaderboard_Filter_FoF);
        fetchLeaderboardFriends();
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override protected int getMenuResource()
    {
        return R.menu.friend_leaderboard_menu;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
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
        detachLeaderboardFriendsCacheListener();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setAdapter(null);
            leaderboardMarkUserListView.setOnItemClickListener(null);
            leaderboardMarkUserListView.setEmptyView(null);
            leaderboardMarkUserListView.addHeaderView(null);
            leaderboardMarkUserListView = null;
        }
        if (leaderboardFriendsUserListAdapter != null)
        {
            leaderboardFriendsUserListAdapter.clear();
            leaderboardFriendsUserListAdapter.setFollowRequestedListener(null);
            leaderboardFriendsUserListAdapter = null;
        }
        if (mProgress != null)
        {
            mProgress = null;
        }

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardFriendsKeyDTOListener = null;
        super.onDestroy();
    }

    private void detachLeaderboardFriendsCacheListener()
    {
        leaderboardFriendsCache.unregister(leaderboardFriendsKeyDTOListener);
    }

    protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
    }

    private void pushInvitationFragment(){
        getDashboardNavigator().pushFragment(FriendsInvitationFragment.class);
    }

    @Override protected void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
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
        detachLeaderboardFriendsCacheListener();
        LeaderboardFriendsKey key = new LeaderboardFriendsKey();
        leaderboardFriendsCache.register(key, this.leaderboardFriendsKeyDTOListener);
        leaderboardFriendsCache.getOrFetchAsync(key);
    }

    private void handleFriendsLeaderboardReceived(LeaderboardFriendsDTO dto)
    {
        Date markingTime = dto.leaderboard.markUtc;
        if (markingTime != null && leaderboardMarkUserMarkingTime != null)
        {
            leaderboardMarkUserMarkingTime.setText(
                    String.format("(%s)", prettyTime.get().format(markingTime)));
        }
        leaderboardFriendsUserListAdapter.add(dto);
        leaderboardFriendsUserListAdapter.notifyDataSetChanged();

    }


    protected class LeaderboardMarkUserListFollowRequestedListener implements LeaderboardFriendsItemView.OnFollowRequestedListener
    {
        @Override public void onFollowRequested(UserBaseKey userBaseKey)
        {
            handleFollowRequested(userBaseKey);
        }
    }

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new LeaderboardMarkUserListPremiumUserFollowedListener();
    }

    protected class LeaderboardMarkUserListPremiumUserFollowedListener extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            handleFollowSuccess(currentUserProfileDTO);
        }
    }

    protected void handleFollowRequested(final UserBaseKey userBaseKey)
    {
        heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                premiumFollowUser(userBaseKey);
            }
        });
    }

    protected void handleFollowSuccess(UserProfileDTO userProfileDTO)
    {
        setCurrentUserProfileDTO(userProfileDTO);
    }

    protected DTOCacheNew.Listener<LeaderboardFriendsKey, LeaderboardFriendsDTO> createFriendsCacheListener()
    {
        return new FriendLeaderboarMarkUserListFragmentCacheListener();
    }

    protected class FriendLeaderboarMarkUserListFragmentCacheListener implements DTOCacheNew.HurriedListener<LeaderboardFriendsKey, LeaderboardFriendsDTO>
    {
        @Override public void onPreCachedDTOReceived(LeaderboardFriendsKey key, LeaderboardFriendsDTO dto)
        {
            handleFriendsLeaderboardReceived(dto);
        }

        @Override public void onDTOReceived(LeaderboardFriendsKey key, LeaderboardFriendsDTO dto)
        {
            mProgress.setVisibility(View.INVISIBLE);
            leaderboardFriendsUserListAdapter.clear();
            handleFriendsLeaderboardReceived(dto);        }

        @Override public void onErrorThrown(LeaderboardFriendsKey key, Throwable error)
        {
            mProgress.setVisibility(View.INVISIBLE);
            if (error instanceof RetrofitError)
            {
                THToast.show(new THException(error));
            }
        }
    }
}