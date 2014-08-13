package com.tradehero.th.api.users;

import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PaginatedAllowableRecipientDTO extends PaginatedDTO<AllowableRecipientDTO>
        implements HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 60;

    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public PaginatedAllowableRecipientDTO()
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public PaginatedAllowableRecipientDTO(@NotNull Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }

    public PaginatedAllowableRecipientDTO(
            PaginationInfoDTO paginationInfoDTO,
            List<AllowableRecipientDTO> allowableRecipientDTOs)
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
        setPagination(paginationInfoDTO);
        setData(allowableRecipientDTOs);
    }

    public PaginatedAllowableRecipientDTO(
            @NotNull Date expirationDate,
            PaginationInfoDTO paginationInfoDTO,
            List<AllowableRecipientDTO> allowableRecipientDTOs)
    {
        this.expirationDate = expirationDate;
        setPagination(paginationInfoDTO);
        setData(allowableRecipientDTOs);
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
