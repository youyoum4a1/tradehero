package com.tradehero.common.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/** Created with IntelliJ IDEA. Date: 7/1/13 Time: 2:33 PM Copyright (c) 2013 thonguyen. All rights reserved. */
public class JacksonConverter implements Converter
{
    private static final String MIME_TYPE = "application/json; charset=UTF-8";
    public static final String TAG = JacksonConverter.class.getSimpleName();

    private final ObjectMapper objectMapper;

    public JacksonConverter(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override public Object fromBody(TypedInput body, final Type type) throws ConversionException
    {
        try
        {
            final JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            return objectMapper.readValue(body.in(), javaType);
        }
        catch (final JsonParseException|JsonMappingException e)
        {
            throw new ConversionException(e);
        }
        catch (final IOException e)
        {
            throw new ConversionException(e);
        }
    }

    @Override public TypedOutput toBody(Object object)
    {
        try
        {
            final String json = objectMapper.writeValueAsString(object);
            return new TypedByteArray(MIME_TYPE, json.getBytes("UTF-8"));
        }
        catch (final JsonProcessingException|UnsupportedEncodingException e)
        {
            throw new AssertionError(e);
        }
    }
}