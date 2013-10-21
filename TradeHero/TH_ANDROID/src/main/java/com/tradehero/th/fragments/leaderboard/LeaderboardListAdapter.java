package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:13 PM Copyright (c) TradeHero */
public class LeaderboardListAdapter extends DTOAdapter<LeaderboardUserDTO, LeaderboardUserDTOView>
{
    public LeaderboardListAdapter(Context context, LayoutInflater layoutInflater, List<LeaderboardUserDTO> items, int layoutResourceId)
    {
        super(context, layoutInflater, layoutResourceId);
        setItems(items);
    }

    @Override protected LeaderboardUserDTOView getView(int position, LeaderboardUserDTOView convertView)
    {
        return convertView;
    }
}
