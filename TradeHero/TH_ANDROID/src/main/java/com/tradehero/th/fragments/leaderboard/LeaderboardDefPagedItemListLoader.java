package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.loaders.PagedItemListLoader;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/18/13 Time: 5:55 PM Copyright (c) TradeHero */
public class LeaderboardDefPagedItemListLoader extends PagedItemListLoader<LeaderboardDefDTO>
{
    public LeaderboardDefPagedItemListLoader(Context context)
    {
        super(context);
    }

    @Override protected void onLoadNextPage(LeaderboardDefDTO lastItemId)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override protected void onLoadPreviousPage(LeaderboardDefDTO startItemId)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public List<LeaderboardDefDTO> loadInBackground()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
