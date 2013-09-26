package com.tradehero.common.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 3:45 PM Copyright (c) TradeHero */
public class CacheHelper extends SQLiteOpenHelper
{
    public CacheHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
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
