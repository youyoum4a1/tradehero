package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import java.util.ArrayList;
import java.util.List;

public class GetPositionsDTO extends AbstractGetPositionsDTO<PositionDTO>
{
    //<editor-fold desc="Constructors">
    public GetPositionsDTO()
    {
        super();
    }

    public GetPositionsDTO(PositionDTOList<PositionDTO> positions, SecurityCompactDTOList securities, int openPositionsCount, int closedPositionsCount)
    {
        super(positions, securities, openPositionsCount, closedPositionsCount);
    }
    //</editor-fold>

    public List<OwnedPositionId> getFiledPositionIds(PortfolioId portfolioId)
    {
        if (positions == null)
        {
            return null;
        }

        List<OwnedPositionId> ownedPositionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positions)
        {
            ownedPositionIds.add(new OwnedPositionId(positionDTO.userId, portfolioId.key, positionDTO.id));
        }

        return ownedPositionIds;
    }
}
