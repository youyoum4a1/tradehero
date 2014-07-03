package com.tradehero.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Scanner;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import retrofit.converter.ConversionException;
import retrofit.converter.SimpleXMLConverter;
import retrofit.mime.TypedInput;

public class CustomXmlConverter extends SimpleXMLConverter
{
    private final Serializer serializer;

    public CustomXmlConverter()
    {
        this(new Persister());
    }

    public CustomXmlConverter(Serializer serializer)
    {
        super(serializer);
        this.serializer = serializer;
    }

    @Override public Object fromBody(TypedInput body, Type type) throws ConversionException
    {
        try
        {
            //System.out.println(">>type "+type+" \n>>mimeType "+body.mimeType());
            //String rawBody = read(body.in());
            //System.out.println("rawBody " + rawBody);
            //return serializer.read((Class<?>) type,rawBody);
            return serializer.read((Class<?>) type, body.in());
        }
        catch (Exception e)
        {
            throw new ConversionException(e);
        }
    }

    private String read(InputStream in)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            //System.err.println("========read error=========");
            //e.printStackTrace();
            return null;
        }
    }

    private String read2(InputStream in)
    {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
