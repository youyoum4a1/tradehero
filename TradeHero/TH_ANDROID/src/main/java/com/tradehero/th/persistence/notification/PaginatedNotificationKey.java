package com.tradehero.th.persistence.notification;

import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import org.jetbrains.annotations.NotNull;

class PaginatedNotificationKey extends PaginatedDTO<NotificationKey>
{
    //<editor-fold desc="Constructors">
    public PaginatedNotificationKey(@NotNull PaginatedNotificationDTO paginatedNotificationDTO)
    {
        setPagination(paginatedNotificationDTO.getPagination());
        setData(new NotificationKeyList(paginatedNotificationDTO.getData()));
    }
    //</editor-fold>
}
