package com.tradehero.common.persistence;

import java.util.List;
import org.jetbrains.annotations.NotNull;

abstract public class BaseDTOListCacheRx<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>>
        extends BaseDTOCacheRx<DTOKeyType, DTOListType>
{
    //<editor-fold desc="Constructors">
    protected BaseDTOListCacheRx(
            int valueSize,
            int subjectSize,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(valueSize, subjectSize, dtoCacheUtil);
    }
    //</editor-fold>
}
