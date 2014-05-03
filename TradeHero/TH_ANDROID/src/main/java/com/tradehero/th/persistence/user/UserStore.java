package com.tradehero.th.persistence.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.UserService;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import retrofit.RetrofitError;


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
