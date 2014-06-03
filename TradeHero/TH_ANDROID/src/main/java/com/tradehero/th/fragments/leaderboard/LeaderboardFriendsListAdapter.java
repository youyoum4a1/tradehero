package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;

public class LeaderboardFriendsListAdapter extends ArrayDTOAdapter<LeaderboardUserDTO, LeaderboardFriendsItemView>
{
    public LeaderboardFriendsListAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, LeaderboardUserDTO leaderboardUserDTO,
            LeaderboardFriendsItemView leaderboardFriendsItemView)
    {
        leaderboardFriendsItemView.setPosition(position);
    }
}
