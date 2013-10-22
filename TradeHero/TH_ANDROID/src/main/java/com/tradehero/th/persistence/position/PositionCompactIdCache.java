package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionCompactId;

import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:03 PM To change this template use File | Settings | File Templates. */
@Singleton public class PositionCompactIdCache extends StraightDTOCache<PositionCompactId, OwnedPositionId>
{
    public static final int DEFAULT_MAX_SIZE = 2000;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected OwnedPositionId fetch(PositionCompactId key)
    {
        throw new IllegalStateException("You should not fetch for OwnedPositionId");
    }
}
