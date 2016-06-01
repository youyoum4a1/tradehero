package com.ayondo.academy.api.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.pagination.PaginatedKey;
import java.util.Map;

public class PaginatedNotificationListKey extends NotificationListKey
    implements PaginatedKey
{
    private static final Integer DEFAULT_PER_PAGE = 42;
    private static final int FIRST_PAGE = 1;

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

    @Override public void putParameters(@NonNull Bundle args)
    {
        super.putParameters(args);

        args.putInt(BUNDLE_PAGE, page);
        args.putInt(BUNDLE_PERPAGE, perPage);
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return super.equals(other) && equalsField(other);
    }

    private boolean equalsField(Object other)
    {
        PaginatedNotificationListKey pagedOther = ((PaginatedNotificationListKey) other);
        return pagedOther.page == page && pagedOther.perPage == perPage;
    }

    @Override public int hashCode()
    {
        return (page * 31) + perPage;
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

    @Override public PaginatedNotificationListKey prev()
    {
        return prev(1);
    }

    @Override public PaginatedNotificationListKey prev(int pages)
    {
        if (page - pages < FIRST_PAGE)
        {
            throw new IllegalArgumentException("Cannot get " + pages + " previous pages from page " + page);
        }
        return new PaginatedNotificationListKey(this, page - pages);
    }
    //</editor-fold>

    @Override public Map<String, Object> toMap()
    {
        Map<String, Object> generatedMap = super.toMap();

        generatedMap.put(PaginatedKey.JSON_PAGE, page);
        generatedMap.put(PaginatedKey.JSON_PERPAGE, DEFAULT_PER_PAGE);
        return generatedMap;
    }
}
