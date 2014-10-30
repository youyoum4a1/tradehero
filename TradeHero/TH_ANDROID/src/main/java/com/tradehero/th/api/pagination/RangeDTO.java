package com.tradehero.th.api.pagination;

public class RangeDTO implements Comparable<RangeDTO>
{
    public final Integer maxCount;
    public final Integer maxId;
    public final Integer minId;

    public static RangeDTO create(Integer maxCount, Integer maxId, Integer minId)
    {
        return new RangeDTO(maxCount, maxId, minId);
    }

    public RangeDTO(Integer maxCount, Integer maxId, Integer minId)
    {
        this.maxCount = maxCount;
        this.maxId = maxId;
        this.minId = minId;
    }

    public RangeDTO copy(RangeDTO newRangeDTO)
    {
        return create(maxCount,
                newRangeDTO.maxId != null ? newRangeDTO.maxId : maxId,
                newRangeDTO.minId != null ? newRangeDTO.minId : minId
        );
    }

    @Override public int hashCode()
    {
        int result = maxCount != null ? maxCount : 0;
        result = 31 * result + (maxId != null ? maxId : 0);
        result = 31 * result + (minId != null ? minId : 0);
        return result;
    }

    @Override public int compareTo(RangeDTO another)
    {
        if (another == null)
        {
            return 1;
        }
        if (maxId != null && another.maxId != null)
        {
            return maxId - another.maxId;
        }

        if (minId != null && another.minId != null)
        {
            return minId - another.minId;
        }
        
        return 0;
    }
}
