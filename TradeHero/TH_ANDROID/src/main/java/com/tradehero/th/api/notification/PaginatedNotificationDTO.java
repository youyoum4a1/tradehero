package com.tradehero.th.api.notification;

import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PaginatedNotificationDTO extends PaginatedDTO<NotificationDTO>
{
    //<editor-fold desc="Constructors">
    public PaginatedNotificationDTO()
    {
    }

    public PaginatedNotificationDTO(
            @NotNull PaginationInfoDTO paginationInfoDTO,
            @NotNull List<NotificationDTO> data)
    {
        setPagination(paginationInfoDTO);
        setData(data);
    }
    //</editor-fold>
}
