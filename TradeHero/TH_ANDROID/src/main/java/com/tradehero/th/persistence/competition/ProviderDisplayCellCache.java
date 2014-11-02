package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.competition.ProviderDisplayCellIdList;
import com.tradehero.th.api.competition.key.ProviderDisplayCellId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache @Deprecated
public class ProviderDisplayCellCache extends StraightDTOCacheNew<ProviderDisplayCellId, ProviderDisplayCellDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public ProviderDisplayCellCache(@NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override @NotNull public ProviderDisplayCellDTO fetch(@NotNull ProviderDisplayCellId key) throws Throwable
    {
        throw new RuntimeException();
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public ProviderDisplayCellDTOList put(@Nullable List<ProviderDisplayCellDTO> providerDisplayCellDTOList)
    {
        if (providerDisplayCellDTOList == null)
        {
            return null;
        }
        ProviderDisplayCellDTOList previous = new ProviderDisplayCellDTOList();
        for (ProviderDisplayCellDTO providerDisplayCellDTO : providerDisplayCellDTOList)
        {
            previous.add(put(providerDisplayCellDTO.getProviderDisplayCellId(), providerDisplayCellDTO));
        }
        return previous;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public ProviderDisplayCellDTOList get(@Nullable ProviderDisplayCellIdList providerDisplayCellIds)
    {
        if (providerDisplayCellIds == null)
        {
            return null;
        }
        ProviderDisplayCellDTOList providerDisplayCellDTOs = new ProviderDisplayCellDTOList(providerDisplayCellIds.expirationDate);
        for (ProviderDisplayCellId providerDisplayCellId : providerDisplayCellIds)
        {
            providerDisplayCellDTOs.add(get(providerDisplayCellId));
        }
        return providerDisplayCellDTOs;
    }
}
