package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.common.persistence.Query;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import com.tradehero.th.loaders.PagedItemListLoader;
import com.tradehero.th.persistence.TimelineStore;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:28 PM Copyright (c) TradeHero */
public class LeaderboardLoader extends PagedItemListLoader<LeaderboardUserRankDTO>
{
    private static final String TAG = LeaderboardLoader.class.getName();
    private Integer minItemId;
    private Integer maxItemId;
    private Integer leaderboardId;

    @Inject
    protected LeaderboardManager leaderboardManager;

    public LeaderboardLoader(Context context, int leaderboardId)
    {
        super(context);
        this.leaderboardId = leaderboardId;
        DaggerUtils.inject(this);
    }

    @Override protected void onLoadNextPage(LeaderboardUserRankDTO lastItemId)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override protected void onLoadPreviousPage(LeaderboardUserRankDTO startItemId)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public List<LeaderboardUserRankDTO> loadInBackground()
    {
        if (minItemId != null)
        {
            ++minItemId;
        }
        if (maxItemId != null)
        {
            --maxItemId;
        }
        THLog.d(TAG, "Start loading leaderboard with maxItemId=" + maxItemId + "/ minItemId=" + minItemId);

        Query query = new Query();
        query.setId(leaderboardId);
        query.setLower(minItemId);
        query.setUpper(maxItemId);
        query.setProperty(TimelineStore.PER_PAGE, itemsPerPage);

        try
        {
            LeaderboardDTO dto = leaderboardManager.getLeaderboard(leaderboardId, true);
            return dto == null ? null : dto.users;
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
