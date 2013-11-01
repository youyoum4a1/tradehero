package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.common.persistence.LeaderboardQuery;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import com.tradehero.th.loaders.PagedItemListLoader;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.leaderboard.LeaderboardStore;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:28 PM Copyright (c) TradeHero */
public class LeaderboardLoader extends PagedItemListLoader<LeaderboardUserRankDTO>
    implements SortTypeChangedListener
{
    private static final String TAG = LeaderboardLoader.class.getName();
    public static final int UNIQUE_LOADER_ID = R.string.loaderboard_loader_id;

    private Integer currentPage;
    private Integer leaderboardId;
    private LeaderboardSortType sortType = LeaderboardSortType.DefaultSortType;

    @Inject
    protected LeaderboardManager leaderboardManager;

    public LeaderboardLoader(Context context, int leaderboardId)
    {
        super(context);
        this.leaderboardId = leaderboardId;
        DaggerUtils.inject(this);
    }

    @Override protected void onLoadNextPage(LeaderboardUserRankDTO firstVisibleItem)
    {
        // do nothing
    }

    @Override protected void onLoadPreviousPage(LeaderboardUserRankDTO lastVisibleItem)
    {
        if (currentPage == null)
        {
            currentPage = 1;
        }
        ++currentPage;
        forceLoad();
    }

    @Override public List<LeaderboardUserRankDTO> loadInBackground()
    {
        THLog.d(TAG, String.format("Start loading leaderboard %d, page=%d", leaderboardId, currentPage));

        LeaderboardQuery query = new LeaderboardQuery();
        query.setId(leaderboardId);
        query.setPage(currentPage);
        query.setSortType(sortType.getFlag());
        query.setProperty(LeaderboardStore.PER_PAGE, getItemsPerPage());

        try
        {
            LeaderboardDTO dto = leaderboardManager.firstOrDefault(query, true);
            return dto == null ? null : dto.users;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @Override public void onSortTypeChange(LeaderboardSortType sortType)
    {
        this.sortType = sortType;
        forceLoad();
    }
}
