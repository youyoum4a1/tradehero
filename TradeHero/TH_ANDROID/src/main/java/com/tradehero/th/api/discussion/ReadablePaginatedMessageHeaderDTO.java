package com.tradehero.th.api.discussion;

import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ReadablePaginatedMessageHeaderDTO extends ReadablePaginatedDTO<MessageHeaderDTO>
{
    //<editor-fold desc="Constructors">
    public ReadablePaginatedMessageHeaderDTO()
    {
    }

    public ReadablePaginatedMessageHeaderDTO(
            PaginationInfoDTO paginationInfoDTO,
            List<MessageHeaderDTO> messageHeaderDTOs)
    {
        setPagination(paginationInfoDTO);
        setData(messageHeaderDTOs);
    }
    //</editor-fold>

    public boolean hasNullItem()
    {
        List<MessageHeaderDTO> data = getData();
        if (data == null)
        {
            return true;
        }
        for (@Nullable MessageHeaderDTO messageHeaderDTO : data)
        {
            if (messageHeaderDTO == null)
            {
                return true;
            }
        }
        return false;
    }
}
