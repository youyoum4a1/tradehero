package com.tradehero.th.api.quote;

import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:34 PM To change this template use File | Settings | File Templates. */
public class QuoteCacheInfoDTO
{
    public static final String TAG = QuoteCacheInfoDTO.class.getSimpleName();

    public String host;
    public int keyCount;
    public Map<String, String> info;
}
