package com.tradehero.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/22/14 Time: 11:32 AM Copyright (c) TradeHero
 */
public class IOUtils
{
    private static final int BUFFER_SIZE = 0x1000;

    /**
     * Copied from Retrofit Utils since they don't expose Utils class
     * Be careful to use this method since the buffer size has only 0x1000 bytes
     * @throws IOException
     */
    public static byte[] streamToBytes(InputStream stream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stream != null)
        {
            byte[] buf = new byte[BUFFER_SIZE];
            int r;
            while ((r = stream.read(buf)) != -1)
            {
                baos.write(buf, 0, r);
            }
        }
        return baos.toByteArray();
    }

    public static byte[] streamToBytes(InputStream stream, int length) throws IOException
    {
        if (stream != null)
        {
            byte[] buf = new byte[length];
            stream.read(buf, 0, length);
            return buf;
        }
        return null;
    }
}
