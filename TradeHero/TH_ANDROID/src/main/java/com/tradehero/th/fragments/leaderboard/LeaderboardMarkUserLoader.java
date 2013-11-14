package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.common.persistence.LeaderboardQuery;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.loaders.PagedItemListLoader;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.leaderboard.LeaderboardStore;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:28 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserLoader extends PagedItemListLoader<LeaderboardUserDTO>
    implements SortTypeChangedListener
{
    private static final String TAG = LeaderboardMarkUserLoader.class.getName();
    public static final int UNIQUE_LOADER_ID = R.string.loaderboard_loader_id;

    private Integer currentPage;
    private Integer leaderboardId;
    private LeaderboardSortType sortType = LeaderboardSortType.DefaultSortType;

    @Inject
    protected LeaderboardManager leaderboardManager;
    private Date markUtc;

    public LeaderboardMarkUserLoader(Context context, int leaderboardId, LeaderboardSortType sortType)
    {
        super(context);
        this.leaderboardId = leaderboardId;
        this.sortType = sortType;
        DaggerUtils.inject(this);
    }

    @Override protected void onLoadNextPage(LeaderboardUserDTO firstVisibleItem)
    {
        // do nothing
    }

    @Override protected void onLoadPreviousPage(LeaderboardUserDTO lastVisibleItem)
    {
        if (currentPage == null)
        {
            currentPage = 1;
        }
        ++currentPage;
        forceLoad();
    }

    @Override public List<LeaderboardUserDTO> loadInBackground()
    {
        THLog.d(TAG, String.format("Loader with id = " + getId()));
        THLog.d(TAG, String.format("Start loading leaderboard %d, page=%d, sortType=%s", leaderboardId, currentPage, sortType.toString()));

        LeaderboardQuery query = new LeaderboardQuery();
        query.setId(leaderboardId);
        query.setPage(currentPage);
        query.setSortType(sortType.getServerFlag());
        query.setProperty(LeaderboardStore.PER_PAGE, getItemsPerPage());

        try
        {
            LeaderboardDTO dto = leaderboardManager.firstOrDefault(query, true);

            if (dto == null)
            {
                return null;
            }

            markUtc = dto.markUtc;
            THLog.d(TAG, "Leaderboard marked at " + dto.markUtc);
            return dto.users;
        }
        catch (IOException e)
        {
            THLog.e(TAG, "Error loading Leaderboard ranking", e);
            return null;
        }
    }

    @Override public void onSortTypeChange(LeaderboardSortType sortType)
    {
        this.sortType = sortType;
        resetQuery();
        forceLoad();
    }

    private void resetQuery()
    {
        currentPage = 1;
        if (items != null)
        {
            items.clear();
        }
    }

    public Date getMarkUtc()
    {
        return markUtc;
    }

    public Integer getLeaderboardId()
    {
        return leaderboardId;
    }
}
