package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ReadablePaginatedMessageHeaderDTO extends ReadablePaginatedDTO<MessageHeaderDTO>
    implements HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 120;

    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public ReadablePaginatedMessageHeaderDTO()
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public ReadablePaginatedMessageHeaderDTO(
            PaginationInfoDTO paginationInfoDTO,
            List<MessageHeaderDTO> messageHeaderDTOs)
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
        setPagination(paginationInfoDTO);
        setData(messageHeaderDTOs);
    }

    public ReadablePaginatedMessageHeaderDTO(
            @NotNull Date expirationDate,
            PaginationInfoDTO paginationInfoDTO,
            List<MessageHeaderDTO> messageHeaderDTOs)
    {
        this.expirationDate = expirationDate;
        setPagination(paginationInfoDTO);
        setData(messageHeaderDTOs);
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
