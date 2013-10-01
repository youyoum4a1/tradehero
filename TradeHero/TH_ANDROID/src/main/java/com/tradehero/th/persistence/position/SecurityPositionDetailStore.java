package com.tradehero.th.persistence.position;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.SecurityService;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public class SecurityPositionDetailStore extends AbstractSecurityPositionDetailStore
{
    private Query query;
    @Inject SecurityService securityService;

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;
    }

    @Override public SecurityPositionDetailDTO loadFrom(Cursor cursor)
    {
        return null;
    }

    @Override public List<SecurityPositionDetailDTO> request()
    {
        if (query == null)
        {
            throw new IllegalArgumentException("Query cannot be null");
        }

        SecurityId securityId = (SecurityId) query.getId();
        if (securityId == null)
        {
            throw new IllegalArgumentException("SecurityId cannot be null");
        }
        SecurityPositionDetailDTO securityPositionDetailDTO = securityService.getSecurity(securityId.exchange, securityId.securitySymbol);
        return Arrays.asList(securityPositionDetailDTO);
    }

    @Override public void setQuery(Query query)
    {
        this.query = query;
    }

    @Override public void store(SQLiteDatabase db, List<SecurityPositionDetailDTO> items)
    {
    }
}
