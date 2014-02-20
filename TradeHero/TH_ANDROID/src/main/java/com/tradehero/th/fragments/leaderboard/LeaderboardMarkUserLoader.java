package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.loaders.PaginationListLoader;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:28 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserLoader extends PaginationListLoader<LeaderboardUserDTO>
{
    protected PagedLeaderboardKey pagedLeaderboardKey;

    @Inject LeaderboardCache leaderboardCache;
    private Date markUtc;

    public LeaderboardMarkUserLoader(Context context, PagedLeaderboardKey pagedLeaderboardKey)
    {
        super(context);
        this.pagedLeaderboardKey = pagedLeaderboardKey;
        DaggerUtils.inject(this);
    }

    @Override protected void onLoadNext(LeaderboardUserDTO firstVisibleItem)
    {
        // do nothing
    }

    @Override protected void onLoadPrevious(LeaderboardUserDTO lastVisibleItem)
    {
        Integer currentPage = pagedLeaderboardKey.page;
        if (currentPage == null)
        {
            currentPage = 1;
        }
        this.pagedLeaderboardKey = pagedLeaderboardKey.cloneAtPage(currentPage + 1);
        forceLoad();
    }

    @Override public List<LeaderboardUserDTO> loadInBackground()
    {
        Timber.d("loadInBackground %s", pagedLeaderboardKey);
        Timber.d("Loader with id = %d", getId());

        try
        {
            LeaderboardDTO fetched = leaderboardCache.getOrFetch(pagedLeaderboardKey);

            if (fetched == null)
            {
                return null;
            }

            markUtc = fetched.markUtc;
            Timber.d("Leaderboard marked at %s", fetched.markUtc);
            return fetched.users;
        }
        catch (Throwable throwable)
        {
            Timber.e("Error loading Leaderboard ranking", throwable);
            return null;
        }
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
        return pagedLeaderboardKey.key;
    }

    public boolean isIncludeFoF()
    {
        return pagedLeaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                ((FriendsPerPagedLeaderboardKey) pagedLeaderboardKey).includeFoF != null &&
                ((FriendsPerPagedLeaderboardKey) pagedLeaderboardKey).includeFoF;
    }
}
