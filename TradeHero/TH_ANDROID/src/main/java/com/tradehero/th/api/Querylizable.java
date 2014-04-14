package com.tradehero.th.api;

/**
 * Created by thonguyen on 3/4/14.
 */

import java.util.Map;

/**
 * This class is for object to can generate Map to be used as a @QueryMap in retrofit call
 */
public interface Querylizable<T>
{
    Map<T, Object> toMap();
}
