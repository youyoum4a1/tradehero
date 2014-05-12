package com.tradehero.common.persistence;

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

    abstract protected DTOCutType cutValue(DTOKeyType key, DTOType value);
    abstract protected DTOType inflateValue(DTOKeyType key, DTOCutType cutValue);

    @Override protected CacheValue<DTOKeyType, DTOType> createCacheValue(DTOKeyType key)
    {
        return new PartialCutCacheValue(key);
    }

    /**
     * This class keeps value null. Only shrunkValue is used.
     */
    protected class PartialCutCacheValue extends PartialCacheValue
    {
        private DTOKeyType key;
        private DTOCutType shrunkValue;

        public PartialCutCacheValue(DTOKeyType key)
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
