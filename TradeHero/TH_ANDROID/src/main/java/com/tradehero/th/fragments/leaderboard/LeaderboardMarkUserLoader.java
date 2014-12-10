package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.th.api.leaderboard.BaseLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.loaders.PaginationListLoader;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public abstract class LeaderboardMarkUserLoader<LeaderboardUserDTOType extends BaseLeaderboardUserDTO> extends PaginationListLoader<LeaderboardUserDTOType>
{
    protected PagedLeaderboardKey pagedLeaderboardKey;
    protected Date markUtc;

    abstract public List<LeaderboardUserDTOType> loadInBackground();

    public LeaderboardMarkUserLoader(Context context, PagedLeaderboardKey pagedLeaderboardKey)
    {
        super(context);
        this.pagedLeaderboardKey = pagedLeaderboardKey;
        HierarchyInjector.inject(context, this);
    }

    @Override protected void onLoadNext(LeaderboardUserDTOType firstVisibleItem)
    {
        // do nothing
    }

    @Override protected void onLoadPrevious(LeaderboardUserDTOType lastVisibleItem)
    {
        Integer currentPage = pagedLeaderboardKey.page;
        if (currentPage == null)
        {
            currentPage = 1;
        }
        this.pagedLeaderboardKey = pagedLeaderboardKey.cloneAtPage(currentPage + 1);
        forceLoad();
    }

    public void setPagedLeaderboardKey(PagedLeaderboardKey pagedLeaderboardKey)
    {
        this.pagedLeaderboardKey = pagedLeaderboardKey;
    }

    public void reload()
    {
        resetQuery();
        forceLoad();
    }

    private void resetQuery()
    {
        this.pagedLeaderboardKey = this.pagedLeaderboardKey.cloneAtPage(1);
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
        return pagedLeaderboardKey.id;
    }

    public boolean isIncludeFoF()
    {
        return pagedLeaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                ((FriendsPerPagedLeaderboardKey) pagedLeaderboardKey).includeFoF != null &&
                ((FriendsPerPagedLeaderboardKey) pagedLeaderboardKey).includeFoF;
    }
}
