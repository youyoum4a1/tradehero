package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 4:08 PM Copyright (c) TradeHero */
public class LeaderboardDefListAdapter extends DTOAdapter<LeaderboardDefDTO, LeaderboardDefView>
{

    public LeaderboardDefListAdapter(Context context, LayoutInflater inflater, List<LeaderboardDefDTO> items, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        setItems(items);
    }

    @Override protected View getView(int position, LeaderboardDefView convertView)
    {
        if (getCount() >= 2)
        {
            if (position == 0)
            {
                convertView.setBackgroundResource(R.drawable.leaderboard_button_border_top);
            }
            else if (position == getCount() - 1)
            {
                convertView.setBackgroundResource(R.drawable.leaderboard_button_border_bottom);
            }
            else
            {
                convertView.setBackgroundResource(R.drawable.leaderboard_button_border_middle);
            }
        }
        else
        {
            convertView.setBackgroundResource(R.drawable.leaderboard_button_border_full);
        }

        return convertView;
    }
}
