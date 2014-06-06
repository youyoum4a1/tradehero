package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;

public class LeaderboardFriendsListAdapter extends ArrayDTOAdapter<LeaderboardUserDTO, LeaderboardFriendsItemView>
{
    protected UserProfileDTO currentUserProfileDTO;
    protected LeaderboardFriendsItemView.OnFollowRequestedListener followRequestedListener;

    public LeaderboardFriendsListAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, LeaderboardUserDTO leaderboardUserDTO,
            LeaderboardFriendsItemView leaderboardFriendsItemView)
    {
        leaderboardFriendsItemView.linkWith(currentUserProfileDTO, true);
        leaderboardFriendsItemView.setFollowRequestedListener(createChildFollowRequestedListener());
        leaderboardFriendsItemView.setPosition(position);
        final View expandingLayout = leaderboardFriendsItemView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.setVisibility(leaderboardUserDTO.isExpanded() ? View.VISIBLE : View.GONE);
        }
    }

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        notifyDataSetChanged();
    }

    protected LeaderboardFriendsItemView.OnFollowRequestedListener createChildFollowRequestedListener()
    {
        return new LeaderboardFriendsItemView.OnFollowRequestedListener()
        {
            @Override public void onFollowRequested(UserBaseKey userBaseKey)
            {
                notifyFollowRequested(userBaseKey);
            }
        };
    }

    protected void notifyFollowRequested(UserBaseKey userBaseKey)
    {
        LeaderboardFriendsItemView.OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseKey);
        }
    }

    public void setFollowRequestedListener(LeaderboardFriendsItemView.OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }
}
