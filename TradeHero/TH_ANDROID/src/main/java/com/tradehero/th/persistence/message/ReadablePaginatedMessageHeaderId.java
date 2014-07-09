package com.tradehero.th.persistence.message;

import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import org.jetbrains.annotations.NotNull;

class ReadablePaginatedMessageHeaderId extends ReadablePaginatedDTO<MessageHeaderId>
{
    public ReadablePaginatedMessageHeaderId(@NotNull ReadablePaginatedMessageHeaderDTO messageHeaderDTOs)
    {
        this.setPagination(messageHeaderDTOs.getPagination());
        this.setData(new MessageHeaderIdList(messageHeaderDTOs.getData()));
    }
}
