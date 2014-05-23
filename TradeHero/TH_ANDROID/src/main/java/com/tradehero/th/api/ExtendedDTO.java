package com.tradehero.th.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class ExtendedDTO implements DTO
{
    public static final boolean VERBOSE = false;

    @JsonIgnore private transient Map<String, Object> extra;
    @JsonIgnore protected transient DateFormat dateFormat;

    //<editor-fold desc="Constructors">
    public ExtendedDTO()
    {
        super();
        extra = new HashMap<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //dateFormat = new ISO8601DateFormat();
    }

    public<ExtendedDTOType extends ExtendedDTO> ExtendedDTO(ExtendedDTOType other, Class<? extends ExtendedDTO> myClass)
    {
        this();
        putAll(other, myClass);
    }
    //</editor-fold>

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

    protected void putAll(ExtendedDTO other, Class<? extends ExtendedDTO> myClass)
    {
        if (other == null)
        {
            return;
        }
        putAll(other.getAll(), myClass);
        Field otherField;
        for (Field myField : myClass.getFields())
        {
            try
            {
                otherField = other.getClass().getField(myField.getName());
                put(myField, otherField.get(other));
            }
            catch (NoSuchFieldException | IllegalAccessException | ParseException e)
            {
                if (VERBOSE)
                {
                    Timber.e("Tried to set field %s from %s", myField.getName(), other, e);
                }
            }
        }
    }

    @JsonAnySetter
    public void put(String key, Object value)
    {
        if (VERBOSE)
        {
            Timber.w("'%s' is not parsed properly in class: '%s'", key, getClass().getName());
        }

        extra.put(key, value);
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

    protected void put(String key, Object value, Class<? extends ExtendedDTO> myClass)
    {
        try
        {
            put(myClass.getDeclaredField(key), value);
        }
        catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | ParseException e)
        {
            if (VERBOSE)
            {
                Timber.e("Tried to set key %s with value %s", key, value, e);
            }
            put(key, value);
        }
    }

    protected void put(Field myField, Object value) throws ParseException, IllegalAccessException
    {
        if (myField.getType().equals(Date.class) && value != null && value instanceof String)
        {
            value = dateFormat.parse((String) value);
            // TODO make it work more generically
            //value = THJsonAdapter.getInstance().fromBody((String) value, field.getType());
        }
        myField.set(this, value);
    }

    protected StringBuilder formatExtras(String repeatSeparator)
    {
        StringBuilder builder = new StringBuilder();
        String separator = "";
        String equals = "=";
        for (Map.Entry<String, Object> entry: extra.entrySet())
        {
            builder.append(separator);
            builder.append(entry.getKey()).append(equals).append(entry.getValue());
            separator = repeatSeparator;
        }
        return builder;
    }

    @Override public String toString()
    {
        return "ExtendedDTO{" + formatExtras(", ").toString() + "}";
    }
}
