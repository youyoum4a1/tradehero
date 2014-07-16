package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

class PaginatedNotificationKey extends PaginatedDTO<NotificationKey>
    implements HasExpiration
{
    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public PaginatedNotificationKey(
            @NotNull PaginatedNotificationDTO paginatedNotificationDTO)
    {
        this.expirationDate = paginatedNotificationDTO.expirationDate;
        this.setPagination(paginatedNotificationDTO.getPagination());
        this.setData(new NotificationKeyList(paginatedNotificationDTO.getData()));
    }
    //</editor-fold>

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                expirationDate.getTime() - Calendar.getInstance().getTime().getTime());
    }
}
