package com.tradehero.th.api;

import com.tradehero.common.persistence.DTO;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 3:58 PM Copyright (c) TradeHero
 */
public class PaginationDTO<T> implements DTO
{
    private List<T> data;



    public List<T> getData()
    {
        return Collections.unmodifiableList(data);
    }

    public void setData(List<T> data)
    {
        this.data = data;
    }
}
