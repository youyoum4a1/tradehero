package com.androidth.general.api.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.HasExpiration;
import com.androidth.general.api.pagination.PaginationInfoDTO;
import com.androidth.general.api.pagination.ReadablePaginatedDTO;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReadablePaginatedMessageHeaderDTO extends ReadablePaginatedDTO<MessageHeaderDTO>
    implements HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 120;

    @NonNull public Date expirationDate;

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
            @NonNull Date expirationDate,
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
