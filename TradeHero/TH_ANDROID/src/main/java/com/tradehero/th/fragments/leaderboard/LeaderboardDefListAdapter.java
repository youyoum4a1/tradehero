package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 4:08 PM Copyright (c) TradeHero */
public class LeaderboardDefListAdapter extends ArrayDTOAdapter<LeaderboardDefDTO, LeaderboardDefView>
{
    private LeaderboardSortType sortType;

    public LeaderboardDefListAdapter(Context context, LayoutInflater inflater, List<LeaderboardDefDTO> items, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        setItems(items);
    }

    @Override public Object getItem(int i)
    {
        LeaderboardDefDTO item = (LeaderboardDefDTO) super.getItem(i);
        item.put(LeaderboardSortType.TAG, sortType);

        return item;
    }

    @Override protected void fineTune(int position, LeaderboardDefDTO dto, LeaderboardDefView dtoView)
    {
        if (getCount() >= 2)
        {
            if (position == 0)
            {
                dtoView.setBackgroundResource(R.drawable.leaderboard_button_border_top);
            }
            else if (position == getCount() - 1)
            {
                dtoView.setBackgroundResource(R.drawable.leaderboard_button_border_bottom);
            }
            else
            {
                dtoView.setBackgroundResource(R.drawable.leaderboard_button_border_middle);
            }
        }
        else
        {
            dtoView.setBackgroundResource(R.drawable.leaderboard_button_border_full);
        }
    }

    public void setSortType(LeaderboardSortType sortType)
    {
        this.sortType = sortType;
    }
}
