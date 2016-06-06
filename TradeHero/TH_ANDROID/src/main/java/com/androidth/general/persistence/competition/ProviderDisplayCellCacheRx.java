package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.competition.ProviderDisplayCellDTO;
import com.androidth.general.api.competition.key.ProviderDisplayCellId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class ProviderDisplayCellCacheRx extends BaseDTOCacheRx<ProviderDisplayCellId, ProviderDisplayCellDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public ProviderDisplayCellCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void put(@NonNull List<ProviderDisplayCellDTO> providerDisplayCellDTOList)
    {
        for (ProviderDisplayCellDTO providerDisplayCellDTO : providerDisplayCellDTOList)
        {
            onNext(providerDisplayCellDTO.getProviderDisplayCellId(), providerDisplayCellDTO);
        }
    }
}
