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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    @InjectView(R.id.leaderboard_mark_user_listview) ListView leaderboardMarkUserListView;
    @InjectView(R.id.progress) ProgressBar mProgress;

    protected LeaderboardFriendsListAdapter leaderboardFriendsUserListAdapter;
    private MiddleCallback<LeaderboardFriendsDTO> getFriendsMiddleCallback;
    private TextView leaderboardMarkUserMarkingTime;
    @Inject LeaderboardServiceWrapper leaderboardServiceWrapper;
    @Inject THLocalyticsSession localyticsSession;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;

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
        detachGetFriendsMiddleCallBack();
        getFriendsMiddleCallback =
                leaderboardServiceWrapper.getNewFriendsLeaderboard(new FriendsCallback());
        mProgress.setVisibility(View.VISIBLE);
    }

    private void detachGetFriendsMiddleCallBack()
    {
        if (getFriendsMiddleCallback != null)
        {
            getFriendsMiddleCallback.setPrimaryCallback(null);
        }
        getFriendsMiddleCallback = null;
    }

    public class FriendsCallback implements Callback<LeaderboardFriendsDTO>
    {
        @Override public void success(LeaderboardFriendsDTO dto, Response response)
        {
            mProgress.setVisibility(View.INVISIBLE);
            Date markingTime = dto.leaderboard.markUtc;
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(
                        String.format("(%s)", prettyTime.get().format(markingTime)));
            }
            leaderboardFriendsUserListAdapter.add(dto);
            leaderboardFriendsUserListAdapter.notifyDataSetChanged();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            mProgress.setVisibility(View.INVISIBLE);
            THToast.show(new THException(retrofitError));
        }
    }

    @Override protected int getMenuResource()
    {
        return R.menu.friend_leaderboard_menu;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.friend_leaderboard_menu:
                getDashboardNavigator().goToTab(DashboardTabType.REFERRAL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        detachGetFriendsMiddleCallBack();
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

    protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
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
}