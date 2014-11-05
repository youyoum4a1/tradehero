package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import com.tradehero.th.api.competition.key.ProviderDisplayCellId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class ProviderDisplayCellCacheRx extends BaseDTOCacheRx<ProviderDisplayCellId, ProviderDisplayCellDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public ProviderDisplayCellCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
