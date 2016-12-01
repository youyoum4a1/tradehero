package com.androidth.general.common.utils;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import retrofit2.Converter;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

public class THJsonAdapter implements Converter<Object, Object>
{
    private static final ConverterType CONVERTER_TYPE = ConverterType.JACKSON;
    private static THJsonAdapter instance = null;
    private JacksonConverterFactory converter;

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
//        converter = ConverterFactory.getConverter(CONVERTER_TYPE);
        converter = JacksonConverterFactory.create();
    }

    public JacksonConverterFactory getConverter(){
        return converter;
    }

//    public static TypedInput toTypedInput(final byte[] jsonBytes)
//    {
//        return new TypedInput()
//        {
//            @Override
//            public String mimeType()
//            {
//                return "application/json; charset=UTF-8";
//            }
//
//            @Override
//            public long length()
//            {
//                return jsonBytes.length;
//            }
//
//            @Override
//            public InputStream in()
//            {
//                return new ByteArrayInputStream(jsonBytes);
//            }
//        };
//    }

//    public static TypedInput toTypedInput(String jsonString)
//    {
//        try
//        {
//            return toTypedInput(jsonString.getBytes("UTF-8"));
//        }
//        catch (IOException ex)
//        {
//            throw new IllegalArgumentException(ex);
//        }
//    }
//
//    public Object fromBody(String body, Type type)
//    {
//        return fromBody(toTypedInput(body), type);
//    }
//
//    public Object fromBody(byte[] json, Type type)
//    {
//        return fromBody(toTypedInput(json), type);
//    }

//    @Override
//    public Object fromBody(TypedInput body, Type type)
//    {
//        try
//        {
//            return converter.requestBodyConverter(body, type);
//        }
//        catch (ConversionException ex)
//        {
//            Timber.e("Conversion error", ex);
//            return null;
//        }
//    }
//
//    @Override public TypedOutput toBody(Object object)
//    {
//        return converter.toBody(object);
//    }




    /*
    RequestBody and ResponseBody
    are the equivalent of TypedOutput and TypedInput.
    */


    public Object fromBody(ResponseBody body) throws IOException {
        return ByteString.read(body.byteStream(), (int) body.contentLength()).utf8();
    }

    public RequestBody toBody(Object object)
    {
        return RequestBody.create(MediaType.parse("text/plain"), object.toString());
    }

    public String toStringBody(Object object) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Sink sink = Okio.sink(byteArrayOutputStream);
        BufferedSink bufferedSink = Okio.buffer(sink);
        toBody(object).writeTo(bufferedSink);
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
        public static JacksonConverterFactory getConverter(ConverterType converterType)
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

        @SuppressLint("SimpleDateFormat")
        private static JacksonConverterFactory getJacksonConverter()
        {
            ObjectMapper objectMapper = new ObjectMapper();
            //objectMapper.setDateFormat(new ISO8601DateFormat());
            //objectMapper.registerModule(new PositionDTOFactory().createPositionDTOModule());
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
//            return new JacksonConverter(objectMapper);
            return JacksonConverterFactory.create(objectMapper);
        }

        //private static Converter getGsonConverter()
        //{
        //    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        //    return new GsonConverter(gson);
        //}
    }

    @Override
    public Object convert(Object value) throws IOException {
        return null;
    }
}
