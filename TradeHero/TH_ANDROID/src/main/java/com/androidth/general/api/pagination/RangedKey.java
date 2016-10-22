package com.androidth.general.api.pagination;

public interface RangedKey
{
    String JSON_MAX_COUNT = "maxCount";
    String JSON_MAX_ID = "maxId";
    String JSON_MIN_ID = "minId";

    Integer getMaxCount();
    Integer getMaxId();
    Integer getMinId();
}
