package com.tradehero.th.fragments.billing.store;

import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.system.SystemStatusCache;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class StoreItemFactory
{
    public static final boolean WITH_IGNORE_SYSTEM_STATUS = true;
    public static final boolean WITH_FOLLOW_SYSTEM_STATUS = false;

    @NotNull private final SystemStatusCache systemStatusCache;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public StoreItemFactory(
            @NotNull SystemStatusCache systemStatusCache,
            @NotNull CurrentUserId currentUserId)
    {
        this.systemStatusCache = systemStatusCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @NotNull public StoreItemDTOList createAll(boolean ignoreSystemStatus)
    {
        StoreItemDTOList created = new StoreItemDTOList();
        return created;
    }
}
