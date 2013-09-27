package com.tradehero.common.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tradehero.th.base.Application;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 3:45 PM Copyright (c) TradeHero */
public class CacheHelper extends SQLiteOpenHelper
{
    private static final String name = "cache.db";
    private static final int version = 8;

    @Inject public CacheHelper(Context context)
    {
        super(context, name, null, version);
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        //db.qu
    }

    @Override public void onUpgrade(SQLiteDatabase db, final int oldVersion, final int newVersion)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
