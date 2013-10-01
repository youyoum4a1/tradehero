package com.tradehero.th.persistence.security;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.UserService;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public class SecurityStore implements PersistableResource<SecurityCompactDTO>
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
        return null;
        //if (query == null)
        //    throw new IllegalArgumentException();
        //
        //SecurityCompactDTO securityCompactDTO = securityService.getSecurity(query.getId());
        //return Arrays.asList(securityCompactDTO);
    }

    @Override public void setQuery(Query query)
    {
        this.query = query;
    }

    @Override public void store(SQLiteDatabase db, List<SecurityCompactDTO> items)
    {
    }
}
