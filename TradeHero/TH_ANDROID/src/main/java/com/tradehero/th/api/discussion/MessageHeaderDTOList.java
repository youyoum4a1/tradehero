package com.tradehero.th.api.discussion;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.HasExpiration;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class MessageHeaderDTOList extends BaseArrayList<MessageHeaderDTO>
    implements DTO, HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public MessageHeaderDTOList()
    {
        super();
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }
    //</editor-fold>

    @NotNull public MessageHeaderIdList createKeys()
    {
        MessageHeaderIdList keys = new MessageHeaderIdList();
        for (@NotNull MessageHeaderDTO messageHeaderDTO : this)
        {
            keys.add(messageHeaderDTO.getDTOKey());
        }
        return keys;
    }

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
