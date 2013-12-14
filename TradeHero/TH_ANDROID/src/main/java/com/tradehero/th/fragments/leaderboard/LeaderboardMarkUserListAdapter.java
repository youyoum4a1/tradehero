package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:13 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserListAdapter extends
        LoaderDTOAdapter<
                LeaderboardUserDTO, LeaderboardMarkUserItemView, LeaderboardMarkUserLoader>
    implements PullToRefreshBase.OnRefreshListener<ListView>
{
    public LeaderboardMarkUserListAdapter(Context context, LayoutInflater inflater, int loaderId, int layoutResourceId)
    {
        super(context, inflater, loaderId, layoutResourceId);
    }

    @Override public Object getItem(int position)
    {
        LeaderboardUserDTO dto = (LeaderboardUserDTO) super.getItem(position);
        dto.setPosition(position);
        dto.setLeaderboardId(getLoader().getLeaderboardId());
        dto.setIncludeFoF(getLoader().isIncludeFoF());

        return dto;
    }

    @Override protected void fineTune(int position, LeaderboardUserDTO dto, LeaderboardMarkUserItemView dtoView)
    {
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        getLoader().loadPrevious();
    }
}
