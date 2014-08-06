package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class StraightCutDTOCacheNew<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOCutType extends DTO>
    extends StraightDTOCacheNew<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    public StraightCutDTOCacheNew(int maxSize)
    {
        super(maxSize);
    }
    //</editor-fold>

    @NotNull abstract protected DTOCutType cutValue(@NotNull DTOKeyType key, @NotNull DTOType value);
    @Nullable abstract protected DTOType inflateValue(@NotNull DTOKeyType key, @Nullable DTOCutType cutValue);

    @Override @NotNull protected CacheValue<DTOKeyType, DTOType> createCacheValue(@NotNull DTOKeyType key)
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

        //<editor-fold desc="Constructors">
        public PartialCutCacheValue(@NotNull DTOKeyType key)
        {
            super();
            this.key = key;
        }
        //</editor-fold>

        @Nullable public DTOCutType getShrunkValue()
        {
            return shrunkValue;
        }

        @Override @Nullable public DTOType getValue()
        {
            return inflateValue(key, shrunkValue);
        }

        @Override public void setValue(@NotNull DTOType value)
        {
            shrunkValue = cutValue(key, value);
        }
    }
}
