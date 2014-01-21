package com.tradehero.th.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.THLog;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 11/15/13 Time: 11:47 AM Copyright (c) TradeHero */
public class ExtendedDTO implements DTO
{
    private static final String TAG = ExtendedDTO.class.getName();

    @JsonIgnore private transient Map<String, Object> extra;

    //<editor-fold desc="Constructors">
    public ExtendedDTO()
    {
        super();
        extra = new HashMap<>();
    }
    //</editor-fold>

    @JsonAnySetter
    public void put(String key, Object value)
    {
        THLog.w(TAG, String.format("'%s' is not parsed properly in class: '%s'", key, getClass().getName()));

        extra.put(key, value);
    }

    protected void put(String key, Object value, Class<? extends ExtendedDTO> myClass)
    {
        try
        {
            Field field = myClass.getDeclaredField(key);
            field.set(this, value);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            THLog.e(TAG, "Tried to set key " + key + " with value " + value, e);
            put(key, value);
        }
    }

    public void putAll(Map<String, Object> pairs, Class<? extends ExtendedDTO> myClass)
    {
        if (pairs == null)
        {
            return;
        }

        for (Map.Entry<String, Object> pair: pairs.entrySet())
        {
            put(pair.getKey(), pair.getValue(), myClass);
        }
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
