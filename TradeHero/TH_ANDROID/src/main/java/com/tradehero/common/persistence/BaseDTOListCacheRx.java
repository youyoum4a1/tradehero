package com.tradehero.common.persistence;

import java.util.List;
import android.support.annotation.NonNull;

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
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(valueSize, subjectSize, dtoCacheUtil);
    }
    //</editor-fold>
}
