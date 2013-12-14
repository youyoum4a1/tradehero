package com.tradehero.th.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 11/15/13 Time: 11:47 AM Copyright (c) TradeHero */
public class ExtendedDTO implements DTO
{
    @JsonIgnore private transient Map<String, Object> extra;

    public ExtendedDTO()
    {
        super();
    }

    public void put(String key, Object value)
    {
        if (extra == null)
        {
            // TODO ConcurrentHashMap?
            extra = new HashMap<>();
        }

        extra.put(key, value);
    }

    public Object get(String key)
    {
        return extra.get(key);
    }

    public Object get(String key, Object defaultValue)
    {
        Object ret = extra.get(key);
        if (ret != null)
        {
            return ret;
        }
        else
        {
            return defaultValue;
        }
    }
}
