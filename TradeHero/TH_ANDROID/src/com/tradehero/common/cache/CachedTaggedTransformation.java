package com.tradehero.common.cache;

import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 12:34 PM To change this template use File | Settings | File Templates. */
public class CachedTaggedTransformation
{
    public String tag;
    public boolean cache;
    public com.fedorvlasov.lazylist.ImageLoader.Transformation transformation;

    public Map<String, CachedTaggedTransformation> subsequent;
}
