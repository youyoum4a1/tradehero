package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PositionCompactCache extends StraightDTOCacheNew<PositionCompactId, PositionDTOCompact>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public PositionDTOCompact fetch(@NotNull PositionCompactId key)
    {
        throw new IllegalStateException("You should not fetch PositionDTOCompact");
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public PositionDTOCompactList put(@Nullable PositionDTOCompactList values)
    {
        if (values == null)
        {
            return null;
        }

        PositionDTOCompactList previousValues = new PositionDTOCompactList();

        for (@NotNull PositionDTOCompact value: values)
        {
            previousValues.add(put(value.getPositionCompactId(), value));
        }

        return previousValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public PositionDTOCompactList get(@Nullable List<PositionCompactId> positionCompactIds)
    {
        if (positionCompactIds == null)
        {
            return null;
        }
        PositionDTOCompactList positionDTOCompacts = new PositionDTOCompactList();

        for (@NotNull PositionCompactId positionCompactId: positionCompactIds)
        {
            positionDTOCompacts.add(get(positionCompactId));
        }

        return positionDTOCompacts;
    }
}
