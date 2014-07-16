package com.tradehero.th.api.notification;

import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaginatedNotificationDTO extends PaginatedDTO<NotificationDTO>
    implements HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 120;

    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public PaginatedNotificationDTO()
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public PaginatedNotificationDTO(
            @NotNull PaginationInfoDTO paginationInfoDTO,
            @NotNull List<NotificationDTO> data)
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
        setPagination(paginationInfoDTO);
        setData(data);
    }

    public PaginatedNotificationDTO(
            @NotNull Date expirationDate,
            @NotNull PaginationInfoDTO paginationInfoDTO,
            @Nullable List<NotificationDTO> data)
    {
        this.expirationDate = expirationDate;
        setPagination(paginationInfoDTO);
        setData(data);
    }
    //</editor-fold>

    protected void setExpirationDateSecondsInFuture(int seconds)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        this.expirationDate = calendar.getTime();
    }

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                expirationDate.getTime() - Calendar.getInstance().getTime().getTime());
    }
}
