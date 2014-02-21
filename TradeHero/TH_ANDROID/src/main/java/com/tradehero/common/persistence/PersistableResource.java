package com.tradehero.common.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 3:44 PM Copyright (c) TradeHero */
public interface PersistableResource<E>
{
    List<E> request();

    void store(SQLiteDatabase db, List<E> items);

    Cursor getCursor(SQLiteDatabase db);

    E loadFrom(Cursor cursor);

    void setQuery(Query query);
}
