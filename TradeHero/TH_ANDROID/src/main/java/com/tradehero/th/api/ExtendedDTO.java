package com.tradehero.th.api;

import android.util.Log;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 11/15/13 Time: 11:47 AM Copyright (c) TradeHero */
public class ExtendedDTO implements DTO
{
    private static final String TAG = ExtendedDTO.class.getName();

    @JsonIgnore private transient Map<String, Object> extra;

    public ExtendedDTO()
    {
        super();
    }

    @JsonAnySetter
    public void put(String key, Object value)
    {
        if (extra == null)
        {
            // TODO ConcurrentHashMap?
            extra = new HashMap<>();
        }

        Log.w(TAG, String.format("'%s' is not parsed properly in class: '%s'", key, getClass().getName()));
        extra.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAll()
    {
        return extra;
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
