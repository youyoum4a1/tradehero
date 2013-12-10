package com.tradehero.common.cache;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 3:41 PM Copyright (c) TradeHero
 *  This class is originally from GitHub android client, modified to adapt dagger
 * */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.common.persistence.PersistableResource;
import dagger.Lazy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Given a PersistableResource, this class will take support loading/storing it's data or requesting fresh data, as appropriate. */
@Singleton public class DatabaseCache
{
    private static final String TAG = DatabaseCache.class.getName();

    @Inject Lazy<CacheHelper> helperProvider;

    /**
     * Get writable database
     *
     * @return writable database or null if it failed to create/open
     */
    protected SQLiteDatabase getWritable(SQLiteOpenHelper helper)
    {
        try
        {
            return helper.getWritableDatabase();
        }
        catch (SQLiteException e1)
        {
            // Make second attempt
            try
            {
                return helper.getWritableDatabase();
            }
            catch (SQLiteException e2)
            {
                return null;
            }
        }
    }

    /**
     * Get readable database
     *
     * @return readable database or null if it failed to create/open
     */
    protected SQLiteDatabase getReadable(SQLiteOpenHelper helper)
    {
        try
        {
            return helper.getReadableDatabase();
        }
        catch (SQLiteException e1)
        {
            // Make second attempt
            try
            {
                return helper.getReadableDatabase();
            }
            catch (SQLiteException e2)
            {
                return null;
            }
        }
    }

    /**
     * Load or request given resources
     *
     * @return resource
     * @throws java.io.IOException
     */
    public <E> List<E> loadOrRequest(PersistableResource<E> persistableResource)
            throws IOException
    {
        SQLiteOpenHelper helper = helperProvider.get();
        try
        {
            List<E> items = loadFromDB(helper, persistableResource);
            if (items != null)
            {
                Log.d(TAG, "CACHE HIT: Found " + items.size() + " items for "
                        + persistableResource);
                return items;
            }
            return requestAndStore(helper, persistableResource);
        }
        finally
        {
            helper.close();
        }
    }

    /**
     * Request and store given resources
     *
     * @return resources
     * @throws IOException
     */
    public <E> List<E> requestAndStore(
            PersistableResource<E> persistableResource) throws IOException
    {
        SQLiteOpenHelper helper = helperProvider.get();
        try
        {
            return requestAndStore(helper, persistableResource);
        }
        finally
        {
            helper.close();
        }
    }

    private <E> List<E> requestAndStore(final SQLiteOpenHelper helper,
            final PersistableResource<E> persistableResource)
            throws IOException
    {
        final List<E> items = persistableResource.request();

        final SQLiteDatabase db = getWritable(helper);
        if (db == null)
        {
            return items;
        }

        db.beginTransaction();
        try
        {
            persistableResource.store(db, items);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
        return items;
    }

    private <E> List<E> loadFromDB(final SQLiteOpenHelper helper,
            final PersistableResource<E> persistableResource)
    {
        final SQLiteDatabase db = getReadable(helper);
        if (db == null)
        {
            return null;
        }

        Cursor cursor = persistableResource.getCursor(db);
        try
        {
            if (cursor == null || !cursor.moveToFirst())
            {
                return null;
            }

            List<E> cached = new ArrayList<E>();
            do
            {
                cached.add(persistableResource.loadFrom(cursor));
            }
            while (cursor.moveToNext());
            return cached;
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
}
