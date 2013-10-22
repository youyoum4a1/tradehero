package com.tradehero.th.persistence.leaderboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.persistence.PaginationFilter;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:41 PM Copyright (c) TradeHero */
public class LeaderboardStore implements PersistableResource<LeaderboardDTO>
{
    private static final String PER_PAGE = "perpage";
    @Inject protected Lazy<LeaderboardService> leaderboardService;
    private Query query;

    @Override public List<LeaderboardDTO> request()
    {
        if (query != null)
        {
            LeaderboardDTO leaderboardDTO = null;
            try
            {
                leaderboardDTO = leaderboardService.get().getLeaderboard((Integer) query.getId());
            }
            catch (RetrofitError retrofitError)
            {
                BasicRetrofitErrorHandler.handle(retrofitError);
            }

            if (leaderboardDTO != null)
            {
                ArrayList<LeaderboardDTO> ret = new ArrayList<>();
                ret.add(leaderboardDTO);
                return ret;
            }
        }

        return null;
    }

    @Override public void store(SQLiteDatabase db, List<LeaderboardDTO> items)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public LeaderboardDTO loadFrom(Cursor cursor)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void setQuery(Query query)
    {
        this.query = query;
    }

    // TODO guice has very nice feature that inject a factory using annotation @Factory
    // need to change this into interface when dagger has similar feature, for now, it's hack :v
    public static class Factory
    {
        @Inject Provider<LeaderboardStore> leaderboardStoreProviders;

        private Map<Integer, LeaderboardStore> stores;

        public Factory()
        {
            stores = new WeakHashMap<>();
        }

        public LeaderboardStore under(int lbId)
        {
            if (stores.get(lbId) == null)
            {
                stores.put(lbId, leaderboardStoreProviders.get());
            }
            return stores.get(lbId);
        }
    }

    public static class LeaderboardFilter extends PaginationFilter
    {
        private int leaderboardId;

        public LeaderboardFilter(int leaderboardId, Comparable maxItemId, Comparable minItemId, int itemsPerPage)
        {
            super(maxItemId, minItemId, itemsPerPage);
            this.leaderboardId = leaderboardId;
        }

        public int getLeaderboardId()
        {
            return leaderboardId;
        }

        public void setLeaderboardId(int leaderboardId)
        {
            this.leaderboardId = leaderboardId;
        }
    }
}
