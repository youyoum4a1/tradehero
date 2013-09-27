package com.tradehero.th.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.Filter;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserService;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 5:43 PM Copyright (c) TradeHero */
public class UserStore implements PersistableResource<UserProfileDTO>
{
    private Filter filter;

    @Override public List<UserProfileDTO> request()
    {
        return null;
    }

    @Override public void store(SQLiteDatabase db, List<UserProfileDTO> items)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public UserProfileDTO loadFrom(Cursor cursor)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class UserFilter
    {
        private int userId;
    }
}
