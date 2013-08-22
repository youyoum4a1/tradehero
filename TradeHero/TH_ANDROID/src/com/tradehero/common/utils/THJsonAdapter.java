package com.tradehero.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA. Date: 7/1/13 Time: 12:49 PM Copyright (c) 2013 thonguyen. All rights
 * reserved.
 */
public class THJsonAdapter implements Converter
{
    private static ConverterType CONVERTER_TYPE = ConverterType.JACKSON;
    private static THJsonAdapter THJSONAdapter = null;
    private Converter converter;

    public static THJsonAdapter getInstance()
    {
        if (THJSONAdapter == null)
        {
            THJSONAdapter = new THJsonAdapter();
        }
        return THJSONAdapter;
    }

    public THJsonAdapter()
    {
        converter = ConverterFactory.getConverter(CONVERTER_TYPE);
    }

    private TypedInput toTypedInput(final byte[] jsonBytes)
    {
        return new TypedInput()
        {
            @Override
            public String mimeType()
            {
                return "application/json; charset=UTF-8";
            }

            @Override
            public long length()
            {
                return jsonBytes.length;
            }

            @Override
            public InputStream in()
            {
                return new ByteArrayInputStream(jsonBytes);
            }
        };
    }

    private TypedInput toTypedInput(String jsonString) throws ConversionException
    {
        try
        {
            return toTypedInput(jsonString.getBytes("UTF-8"));
        }
        catch (IOException ex)
        {
            throw new ConversionException(ex);
        }
    }

    public Object fromBody(String body, Type type) throws ConversionException
    {
        return fromBody(toTypedInput(body), type);
    }

    public Object fromBody(byte[] json, Type type) throws ConversionException
    {
        return fromBody(toTypedInput(json), type);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException
    {
        return converter.fromBody(body, type);
    }

    @Override
    public TypedOutput toBody(Object object)
    {
        return converter.toBody(object);
    }

    private enum ConverterType
    {
        GSON, JACKSON
    }

    public static class ConverterFactory
    {
        public static Converter getConverter(ConverterType converterType)
        {
            switch (ConverterType.GSON)
            {
                case GSON:
                    return getGsonConverter();
                case JACKSON:
                    return getJacksonConverter();
            }
            return null;
        }

        private static Converter getJacksonConverter()
        {
            return new JacksonConverter(new ObjectMapper());
        }

        private static Converter getGsonConverter()
        {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            return new GsonConverter(gson);
        }
    }
}
