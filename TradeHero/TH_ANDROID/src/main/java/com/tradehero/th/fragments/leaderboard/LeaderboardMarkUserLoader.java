package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.common.persistence.LeaderboardQuery;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.PaginationListLoader;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.leaderboard.LeaderboardStore;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:28 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserLoader extends PaginationListLoader<LeaderboardUserDTO>
{
    private static final String TAG = LeaderboardMarkUserLoader.class.getName();
    public static final int UNIQUE_LOADER_ID = R.string.loaderboard_loader_id;

    private Integer currentPage;
    private Integer leaderboardId;
    private boolean includeFoF;
    private LeaderboardSortType sortType = LeaderboardSortType.DefaultSortType;

    @Inject
    protected LeaderboardManager leaderboardManager;
    private Date markUtc;

    public LeaderboardMarkUserLoader(Context context, int leaderboardId, LeaderboardSortType sortType, boolean includeFoF)
    {
        super(context);
        this.leaderboardId = leaderboardId;
        this.sortType = sortType;
        this.includeFoF = includeFoF;
        DaggerUtils.inject(this);
    }

    @Override protected void onLoadNext(LeaderboardUserDTO firstVisibleItem)
    {
        // do nothing
    }

    @Override protected void onLoadPrevious(LeaderboardUserDTO lastVisibleItem)
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
        query.setProperty(LeaderboardStore.INCLUDE_FRIENDS_OF_FRIENDS, includeFoF);
        query.setProperty(LeaderboardStore.PER_PAGE, getPerPage());

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

    public void setSortType(LeaderboardSortType sortType)
    {
        this.sortType = sortType;
    }

    public void reload()
    {
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

    public void setIncludeFoF(boolean includeFoF)
    {
        this.includeFoF = includeFoF;
    }

    public boolean isIncludeFoF()
    {
        return includeFoF;
    }
}
