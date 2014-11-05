package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract public class StraightCutDTOCacheNew<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOCutType extends DTO>
    extends StraightDTOCacheNew<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    public StraightCutDTOCacheNew(int maxSize, @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    @NonNull abstract protected DTOCutType cutValue(@NonNull DTOKeyType key, @NonNull DTOType value);
    @Nullable abstract protected DTOType inflateValue(@NonNull DTOKeyType key, @Nullable DTOCutType cutValue);

    @Override @NonNull protected CacheValue<DTOKeyType, DTOType> createCacheValue(@NonNull DTOKeyType key)
    {
        return new PartialCutCacheValue(key);
    }

    /**
     * This class keeps value null. Only shrunkValue is used.
     */
    protected class PartialCutCacheValue extends PartialCacheValue
    {
        @NonNull private final DTOKeyType key;
        @Nullable private DTOCutType shrunkValue;

        //<editor-fold desc="Constructors">
        public PartialCutCacheValue(@NonNull DTOKeyType key)
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

        @Override public void setValue(@NonNull DTOType value)
        {
            shrunkValue = cutValue(key, value);
        }
    }
}
