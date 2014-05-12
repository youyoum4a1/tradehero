package com.tradehero.common.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;


public interface PersistableResource<E>
{
    List<E> request();

    void store(SQLiteDatabase db, List<E> items);

    Cursor getCursor(SQLiteDatabase db);

    E loadFrom(Cursor cursor);

    void setQuery(Query query);
}
