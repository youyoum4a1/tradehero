package com.tradehero.th.api.pagination;


public interface RangedKey
{
    static final String JSON_MAX_COUNT = "maxCount";
    static final String JSON_MAX_ID = "maxId";
    static final String JSON_MIN_ID = "minId";

    Integer getMaxCount();
    Integer getMaxId();
    Integer getMinId();
}
