package com.tradehero.th.api;

import com.tradehero.common.persistence.DTO;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 3:58 PM Copyright (c) TradeHero
 */
public class PaginatedDTO<ListedType> implements DTO
{
    private List<ListedType> data;
    private PaginationInfoDTO pagination;

    public List<ListedType> getData()
    {
        return Collections.unmodifiableList(data);
    }

    public void setData(List<ListedType> data)
    {
        this.data = data;
    }

    public PaginationInfoDTO getPagination()
    {
        return pagination;
    }

    public void setPagination(PaginationInfoDTO pagination)
    {
        this.pagination = pagination;
    }
}
