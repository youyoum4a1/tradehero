package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.HasExpiration;
import com.androidth.general.api.pagination.PaginatedDTO;
import java.util.Calendar;
import java.util.Date;

public class PaginatedAllowableRecipientDTO extends PaginatedDTO<AllowableRecipientDTO>
        implements HasExpiration, ContainerDTO<AllowableRecipientDTO, AllowableRecipientDTOList>
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 60;

    @NonNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public PaginatedAllowableRecipientDTO()
    {
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
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

    @Override public int size()
    {
        return getData().size();
    }

    @Override public AllowableRecipientDTOList getList()
    {
        return new AllowableRecipientDTOList(getData());
    }
}
