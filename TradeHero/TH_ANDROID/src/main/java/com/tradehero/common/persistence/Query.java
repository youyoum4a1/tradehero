package com.tradehero.common.persistence;

import java.util.HashMap;
import java.util.Map;

public class Query
{
    private static final String idKey = Query.class.getName() + ".id";
    private static final String upperKey = Query.class.getName() + ".upper";
    private static final String lowerKey = Query.class.getName() + ".lower";

    private Map<String, Object> properties = new HashMap<>();

    public Comparable getId()
    {
        return (Comparable)getProperty(idKey);
    }

    public void setId(Comparable id)
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
