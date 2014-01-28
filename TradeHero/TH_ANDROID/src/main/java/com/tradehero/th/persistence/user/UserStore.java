package com.tradehero.th.persistence.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.retrofit.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.UserService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 5:43 PM Copyright (c) TradeHero */
public class UserStore extends AbstractUserStore
{
    private Query query;
    @Inject UserService userService;

    @Override public List<UserProfileDTO> request()
    {
        if (query == null)
            throw new IllegalArgumentException();

        try
        {
            return Arrays.asList(userService.getUser((Integer) query.getId()));
        }
        catch (RetrofitError error)
        {
            BasicRetrofitErrorHandler.handle(error);
        }
        return null;
    }

    @Override public void store(SQLiteDatabase db, List<UserProfileDTO> items)
    {
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;
    }

    @Override public UserProfileDTO loadFrom(Cursor cursor)
    {
        return null;
    }

    @Override public void setQuery(Query query)
    {
        this.query = query;
    }
}
