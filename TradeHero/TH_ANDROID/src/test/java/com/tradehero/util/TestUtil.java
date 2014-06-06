package com.tradehero.util;

import com.tradehero.common.utils.IOUtils;
import java.io.IOException;

public class TestUtil
{
    public static byte[] getResourceAsByteArray(Class clz, String resourcePath) throws IOException
    {
        return IOUtils.streamToBytes(clz.getResourceAsStream(resourcePath));
    }
}
