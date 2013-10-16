package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.FiledPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class FiledPositionCache extends StraightDTOCache<String, FiledPositionId, PositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 5000;

    @Inject Lazy<PositionCompactIdCache> positionCompactIdCache;

    //<editor-fold desc="Constructors">
    @Inject public FiledPositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected PositionDTO fetch(FiledPositionId key)
    {
        throw new IllegalStateException("You should not fetch PositionDTO individually");
    }

    @Override public PositionDTO put(FiledPositionId key, PositionDTO value)
    {
        // Save the correspondence between integer id and compound key.
        positionCompactIdCache.get().put(value.getPositionCompactId(), key);

        return super.put(key, value);
    }
}
