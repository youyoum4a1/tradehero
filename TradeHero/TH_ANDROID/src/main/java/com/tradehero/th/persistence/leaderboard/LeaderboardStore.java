package com.tradehero.th.persistence.leaderboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.LeaderboardQuery;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.network.retrofit.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;


public class LeaderboardStore implements PersistableResource<LeaderboardDTO>
{
    public static final String INCLUDE_FRIENDS_OF_FRIENDS = "includeFoF";
    public static final String PER_PAGE = "perPage";
    private static final Integer DEFAULT_PER_PAGE = 42;

    @Inject protected Lazy<LeaderboardService> leaderboardService;
    @Inject protected Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;

    private LeaderboardQuery query;

    @Override public List<LeaderboardDTO> request()
    {
        if (query != null)
        {
            LeaderboardDTO leaderboardDTO = null;
            try
            {
                Integer leaderboardId = (Integer) query.getId();
                Integer perPage = (Integer) query.getProperty(PER_PAGE);
                Boolean includeFoF = (Boolean) query.getProperty(INCLUDE_FRIENDS_OF_FRIENDS);
                if (perPage == null)
                {
                    perPage = DEFAULT_PER_PAGE;
                }

                if (leaderboardId != null)
                {
                    switch (leaderboardId)
                    {
                        case LeaderboardDefDTO.LEADERBOARD_FRIEND_ID:
                            leaderboardDTO = leaderboardService.get().getFriendsLeaderboard(query.getPage(), perPage, includeFoF, query.getSortType());
                            break;
                        default:
                            leaderboardDTO = leaderboardService.get().getLeaderboard(leaderboardId, query.getPage(), perPage, query.getSortType());
                    }
                }
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
        if (query instanceof LeaderboardQuery)
        {
            this.query = (LeaderboardQuery) query;
        }
        else
        {
            throw new IllegalArgumentException("Query is not an instance of Leaderboard Query");
        }
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
}
