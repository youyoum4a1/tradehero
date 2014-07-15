package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GetPositionsCutDTO implements DTO
{
    @Nullable public final List<PositionDTOKey> ownedPositionIds;
    @Nullable public final List<SecurityId> securityIds;
    public final int openPositionsCount;
    public final int closedPositionsCount;

    public GetPositionsCutDTO(
            @NotNull GetPositionsDTO getPositionsDTO,
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull PositionCache positionCache)
    {
        positionCache.put(getPositionsDTO.positions);
        this.ownedPositionIds = PositionDTO.getFiledPositionIds(getPositionsDTO.positions);

        if (getPositionsDTO.securities != null)
        {
            securityCompactCache.put(getPositionsDTO.securities);
        }
        this.securityIds = SecurityCompactDTO.getSecurityIds(getPositionsDTO.securities);

        this.openPositionsCount = getPositionsDTO.openPositionsCount;
        this.closedPositionsCount = getPositionsDTO.closedPositionsCount;
    }

    @Nullable
    public GetPositionsDTO create(
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull PositionCache positionCache)
    {
        PositionDTOList<PositionDTO> cachedPositionDTOs = positionCache.get(ownedPositionIds);
        if (cachedPositionDTOs != null && cachedPositionDTOs.hasNullItem())
        {
            return null;
        }
        SecurityCompactDTOList securityCompactDTOs = securityCompactCache.get(securityIds);
        if (securityCompactDTOs != null && securityCompactDTOs.hasNullItem())
        {
            return null;
        }
        return new GetPositionsDTO(
                cachedPositionDTOs,
                securityCompactDTOs,
                openPositionsCount,
                closedPositionsCount
        );
    }
}
