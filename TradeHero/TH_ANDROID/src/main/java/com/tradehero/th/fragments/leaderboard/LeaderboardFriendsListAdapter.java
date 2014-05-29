package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;

public class LeaderboardFriendsListAdapter extends ArrayDTOAdapter<LeaderboardUserDTO, LeaderboardFriendsItemView>
{
    //private OnPremiumFollowRequestedListener premiumFollowRequestedListener;

    public LeaderboardFriendsListAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, LeaderboardUserDTO leaderboardUserDTO,
            LeaderboardFriendsItemView leaderboardFriendsItemView)
    {
        //relationsListItemView.setPremiumFollowRequestedListener(createFollowRequestedListener());
        leaderboardFriendsItemView.setPosition(position);
    }

    //public void setPremiumFollowRequestedListener(
    //        OnPremiumFollowRequestedListener premiumFollowRequestedListener)
    //{
    //    this.premiumFollowRequestedListener = premiumFollowRequestedListener;
    //}
    //
    //protected void notifyFollowRequested(UserBaseKey userBaseKey)
    //{
    //    OnPremiumFollowRequestedListener listener = premiumFollowRequestedListener;
    //    if (listener != null)
    //    {
    //        listener.premiumFollowRequested(userBaseKey);
    //    }
    //}
    //
    //protected OnPremiumFollowRequestedListener createFollowRequestedListener()
    //{
    //    return new RelationsListItemAdapterFollowRequestedListener();
    //}
    //
    //protected class RelationsListItemAdapterFollowRequestedListener implements
    //        OnPremiumFollowRequestedListener
    //{
    //    @Override public void premiumFollowRequested(UserBaseKey userBaseKey)
    //    {
    //        notifyFollowRequested(userBaseKey);
    //    }
    //}
}
