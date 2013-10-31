package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ExpandableDTOAdapter;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:13 PM Copyright (c) TradeHero */
public class LeaderboardListAdapter extends
        //DTOAdapter<LeaderboardUserRankDTO, LeaderboardUserRankItemView>
        ExpandableDTOAdapter<LeaderboardUserRankDTO, LeaderboardListAdapter.ExpandableLeaderboardUserRankItem, LeaderboardUserRankItemView>
{
    public LeaderboardListAdapter(Context context, LayoutInflater layoutInflater, List<LeaderboardUserRankDTO> items, int layoutResourceId)
    {
        super(context, layoutInflater, layoutResourceId);
        //setItems(items);
    }

    @Override public Object getItem(int i)
    {
        LeaderboardUserRankDTO dto = (LeaderboardUserRankDTO) super.getItem(i);
        dto.setRank(i);
        return dto;
    }

    @Override protected void fineTune(int position, ExpandableLeaderboardUserRankItem dto, LeaderboardUserRankItemView dtoView)
    {
        // Nothing to do
    }

    public static class ExpandableLeaderboardUserRankItem extends ExpandableListItem<LeaderboardUserRankDTO>
    {
        public ExpandableLeaderboardUserRankItem(LeaderboardUserRankDTO model)
        {
            super(model);
        }
    }
}
