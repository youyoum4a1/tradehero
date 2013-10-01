package com.tradehero.th.persistence.security;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.network.service.SecurityService;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public class SecurityCompactStore extends AbstractSecurityCompactStore
{
    private Query query;
    @Inject SecurityService securityService;

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;
    }

    @Override public SecurityCompactDTO loadFrom(Cursor cursor)
    {
        return null;
    }

    @Override public List<SecurityCompactDTO> request()
    {
        if (query != null)
        {
            if (query instanceof SecurityTrendingQuery)
            {
                return requestTrending();
            }

            if (query instanceof SecuritySearchQuery)
            {
                return requestSearch((SecuritySearchQuery) query);
            }

            throw new IllegalArgumentException("Query type unknown " + query.getClass().getName());
        }

        throw new IllegalArgumentException("SecurityId cannot be null");
    }

    private List<SecurityCompactDTO> requestTrending()
    {
        return securityService.getTrendingSecurities();
    }

    private List<SecurityCompactDTO> requestSearch(SecuritySearchQuery securitySearchQuery)
    {
        return securityService.searchSecurities(securitySearchQuery.getSearchString(), securitySearchQuery.getPage(), securitySearchQuery.getPerPage());
    }

    @Override public void setQuery(Query query)
    {
        this.query = query;
    }

    @Override public void store(SQLiteDatabase db, List<SecurityCompactDTO> items)
    {
    }
}
