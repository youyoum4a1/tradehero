package com.tradehero.th.api.notification;

import android.os.Bundle;
import com.tradehero.th.api.PaginatedKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thonguyen on 3/4/14.
 */
public class PaginatedNotificationListKey extends NotificationListKey
    implements PaginatedKey
{
    private static final Integer DEFAULT_PER_PAGE = 42;

    private int page;

    //<editor-fold desc="Constructors">

    public PaginatedNotificationListKey(Integer key)
    {
        super(key);
    }

    public PaginatedNotificationListKey(Bundle args)
    {
        super(args);

        page = args.getInt(PAGE, 0);
    }

    public PaginatedNotificationListKey(NotificationListKey notificationListKey, int page)
    {
        super(notificationListKey.getArgs());

        this.page = page;
    }
    //</editor-fold>

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(PAGE, page);
    }

    @Override public boolean equals(Object other)
    {
        return super.equals(other) && ((PaginatedNotificationListKey) other).page == page;
    }

    //<editor-fold desc="PaginatedKey">
    @Override public int getPage()
    {
        return page;
    }

    @Override public PaginatedNotificationListKey next()
    {
        return next(1);
    }

    @Override public PaginatedNotificationListKey next(int pages)
    {
        return new PaginatedNotificationListKey(this, page + pages);
    }
    //</editor-fold>

    @Override public Map<String, Object> toMap()
    {
        Map<String, Object> generatedMap = super.toMap();
        if (generatedMap == null)
        {
            generatedMap = new HashMap<>();
        }

        generatedMap.put("page", page);
        generatedMap.put("perPage", DEFAULT_PER_PAGE);
        return generatedMap;
    }
}
