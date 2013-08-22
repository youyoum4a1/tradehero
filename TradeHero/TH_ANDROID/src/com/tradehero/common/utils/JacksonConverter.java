package com.tradehero.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created with IntelliJ IDEA. Date: 7/1/13 Time: 2:33 PM Copyright (c) 2013 thonguyen. All rights
 * reserved.
 */
public class JacksonConverter implements Converter
{
    /**
     * A {@link retrofit.converter.Converter} which uses Jackson for serialization and
     * deserialization of entities.
     */
    private final ObjectMapper jacksonObjectMapper;

    public JacksonConverter(ObjectMapper jacksonObjectMapper)
    {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException
    {
        String charset = "UTF-8";
        if (body.mimeType() != null)
        {
            charset = MimeUtil.parseCharset(body.mimeType());
        }
        InputStreamReader isr = null;
        try
        {
            isr = new InputStreamReader(body.in(), charset);
            return jacksonObjectMapper.readValue(isr, type.getClass());
        }
        catch (IOException e)
        {
            throw new ConversionException(e);
        }
        finally
        {
            if (isr != null)
            {
                try
                {
                    isr.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object)
    {
        try
        {
            return new JsonTypedOutput(jacksonObjectMapper.writeValueAsBytes(object));
        }
        catch (JsonProcessingException e)
        {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput
    {
        private final byte[] jsonBytes;

        JsonTypedOutput(byte[] jsonBytes)
        {
            this.jsonBytes = jsonBytes;
        }

        @Override
        public String fileName()
        {
            return null;
        }

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
        public void writeTo(OutputStream out) throws IOException
        {
            out.write(jsonBytes);
        }
    }
}