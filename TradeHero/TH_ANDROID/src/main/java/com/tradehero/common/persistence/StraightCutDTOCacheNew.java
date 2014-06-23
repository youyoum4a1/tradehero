package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class StraightCutDTOCacheNew<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOCutType extends DTO>
    extends StraightDTOCacheNew<DTOKeyType, DTOType>
{
    public StraightCutDTOCacheNew(int maxSize)
    {
        super(maxSize);
    }

    @NotNull abstract protected DTOCutType cutValue(@NotNull DTOKeyType key, @NotNull DTOType value);
    @Nullable abstract protected DTOType inflateValue(@NotNull DTOKeyType key, @Nullable DTOCutType cutValue);

    @Override protected CacheValue<DTOKeyType, DTOType> createCacheValue(DTOKeyType key)
    {
        return new PartialCutCacheValue(key);
    }

    /**
     * This class keeps value null. Only shrunkValue is used.
     */
    protected class PartialCutCacheValue extends PartialCacheValue
    {
        @NotNull private final DTOKeyType key;
        @Nullable private DTOCutType shrunkValue;

        public PartialCutCacheValue(@NotNull DTOKeyType key)
        {
            super();
            this.key = key;
        }

        @Override public DTOType getValue()
        {
            return inflateValue(key, shrunkValue);
        }

        @Override public void setValue(DTOType value)
        {
            shrunkValue = cutValue(key, value);
        }
    }
}
