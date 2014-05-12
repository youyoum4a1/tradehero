package com.tradehero.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import timber.log.Timber;

public class THJsonAdapter implements Converter
{
    private static ConverterType CONVERTER_TYPE = ConverterType.JACKSON;
    private static THJsonAdapter instance = null;
    private Converter converter;

    public static THJsonAdapter getInstance()
    {
        if (instance == null)
        {
            instance = new THJsonAdapter();
        }
        return instance;
    }

    public THJsonAdapter()
    {
        converter = ConverterFactory.getConverter(CONVERTER_TYPE);
    }

    public static TypedInput toTypedInput(final byte[] jsonBytes)
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

    public static TypedInput toTypedInput(String jsonString)
    {
        try
        {
            return toTypedInput(jsonString.getBytes("UTF-8"));
        }
        catch (IOException ex)
        {
            throw new IllegalArgumentException(ex);
        }
    }

    public Object fromBody(String body, Type type)
    {
        return fromBody(toTypedInput(body), type);
    }

    public Object fromBody(byte[] json, Type type)
    {
        return fromBody(toTypedInput(json), type);
    }

    @Override
    public Object fromBody(TypedInput body, Type type)
    {
        try
        {
            return converter.fromBody(body, type);
        }
        catch (ConversionException ex)
        {
            Timber.e("Conversion error", ex);
            return null;
        }
    }

    @Override public TypedOutput toBody(Object object)
    {
        return converter.toBody(object);
    }

    public String toStringBody(Object object) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        toBody(object).writeTo(byteArrayOutputStream);
        String body = byteArrayOutputStream.toString("UTF-8");
        byteArrayOutputStream.close();
        return body;
    }

    private enum ConverterType
    {
        GSON, JACKSON
    }

    public static class ConverterFactory
    {
        public static Converter getConverter(ConverterType converterType)
        {
            switch (converterType)
            {
                //case GSON:
                //    return getGsonConverter();
                case JACKSON:
                    return getJacksonConverter();
            }
            return null;
        }

        private static Converter getJacksonConverter()
        {
            ObjectMapper objectMapper = new ObjectMapper();
            //objectMapper.setDateFormat(new ISO8601DateFormat());
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            return new JacksonConverter(objectMapper);
        }

        //private static Converter getGsonConverter()
        //{
        //    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        //    return new GsonConverter(gson);
        //}
    }
}
