package com.tradehero.common.cache;

import com.fedorvlasov.lazylist.FileCache;
import com.tradehero.th.application.App;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 3:25 PM To change this template use File | Settings | File Templates. */
public class FileCacheFactory
{
    public static FileCache createFileCache ()
    {
        return new FileCache(App.context());
    }

    public static FileCache createFileCache (String dirSuffix)
    {
        return new FileCache(App.context(), dirSuffix);
    }
}
