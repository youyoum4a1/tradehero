package com.tradehero.common.persistence;

import java.util.List;

abstract public class BaseDTOListCacheRx<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>>
        extends BaseDTOCacheRx<DTOKeyType, DTOListType>
{
    //<editor-fold desc="Constructors">
    protected BaseDTOListCacheRx(int valueSize, int subjectSize)
    {
        super(valueSize, subjectSize);
    }
    //</editor-fold>
}
