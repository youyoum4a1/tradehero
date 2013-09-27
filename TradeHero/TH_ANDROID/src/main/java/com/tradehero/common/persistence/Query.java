package com.tradehero.common.persistence;

import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 9/27/13 Time: 11:34 AM Copyright (c) TradeHero */
public class Query
{
    private static final String idKey = "id";
    private static final String upperKey = "upper";
    private static final String lowerKey = "lower";

    private Map<String, Object> properties = new HashMap<>();

    public Integer getId()
    {
        return (Integer)getProperty(idKey);
    }

    public void setId(Integer id)
    {
        setProperty(idKey, id);
    }

    public Object getProperty(String prop)
    {
        return properties.get(prop);
    }

    public void setProperty(String prop, Object value)
    {
        properties.put(prop, value);
    }

    public Integer getUpper()
    {
        return (Integer)getProperty(upperKey);
    }

    public void setUpper(Integer upper)
    {
        setProperty(upperKey, upper);
    }

    public Integer getLower()
    {
        return (Integer)getProperty(lowerKey);
    }

    public void setLower(Integer lower)
    {
        setProperty(lowerKey, lower);
    }
}
