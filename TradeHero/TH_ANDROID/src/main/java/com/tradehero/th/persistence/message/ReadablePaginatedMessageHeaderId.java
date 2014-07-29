package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

class ReadablePaginatedMessageHeaderId extends ReadablePaginatedDTO<MessageHeaderId>
    implements HasExpiration
{
    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public ReadablePaginatedMessageHeaderId(
            @NotNull ReadablePaginatedMessageHeaderDTO messageHeaderDTOs)
    {
        this.expirationDate = messageHeaderDTOs.expirationDate;
        this.setPagination(messageHeaderDTOs.getPagination());
        this.setData(new MessageHeaderIdList(messageHeaderDTOs.getData()));
    }
    //</editor-fold>

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                expirationDate.getTime() - Calendar.getInstance().getTime().getTime());
    }
}
