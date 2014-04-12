package com.tradehero.th.api.notification;

import android.os.Bundle;
import com.tradehero.th.api.pagination.PaginatedKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thonguyen on 3/4/14.
 */
public class PaginatedNotificationListKey extends NotificationListKey
    implements PaginatedKey
{
    private static final Integer DEFAULT_PER_PAGE = 42;

    private final Integer perPage;
    private final int page;

    //<editor-fold desc="Constructors">

    public PaginatedNotificationListKey(Integer key)
    {
        super(key);

        page = 0;
        perPage = DEFAULT_PER_PAGE;
    }

    public PaginatedNotificationListKey(Bundle args)
    {
        super(args);

        page = args.getInt(BUNDLE_PAGE, 0);
        perPage = args.getInt(BUNDLE_PERPAGE, DEFAULT_PER_PAGE);
    }

    public PaginatedNotificationListKey(NotificationListKey notificationListKey, int page)
    {
        super(notificationListKey.getArgs());

        this.page = page;
        this.perPage = DEFAULT_PER_PAGE;
    }
    //</editor-fold>

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);

        args.putInt(BUNDLE_PAGE, page);
        args.putInt(BUNDLE_PERPAGE, perPage);
    }

    @Override public boolean equals(Object other)
    {
        return super.equals(other) && equalsField(other);
    }

    private boolean equalsField(Object other)
    {
        PaginatedNotificationListKey pagedOther = ((PaginatedNotificationListKey) other);
        return pagedOther.page == page && pagedOther.perPage == perPage;
    }

    //<editor-fold desc="PaginatedKey">
    @Override public Integer getPage()
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

        generatedMap.put(PaginatedKey.JSON_PAGE, page);
        generatedMap.put(PaginatedKey.JSON_PERPAGE, DEFAULT_PER_PAGE);
        return generatedMap;
    }
}
