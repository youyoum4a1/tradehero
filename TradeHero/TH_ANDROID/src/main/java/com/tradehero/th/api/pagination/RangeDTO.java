package com.tradehero.th.api.pagination;


public class RangeDTO
{
    public final Integer maxCount;
    public final Integer maxId;
    public final Integer minId;

    public RangeDTO(Integer maxCount, Integer maxId, Integer minId)
    {
        this.maxCount = maxCount;
        this.maxId = maxId;
        this.minId = minId;
    }
}
