package com.tradehero.util;

import com.tradehero.common.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;

public class TestUtil
{
    public static byte[] getResourceAsByteArray(Class clz, String resourcePath) throws IOException
    {
        InputStream is = null;

        try
        {
            is = clz.getResourceAsStream(resourcePath);
            return IOUtils.streamToBytes(is);
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }
}
