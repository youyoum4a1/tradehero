package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:35 PM To change this template use File | Settings | File Templates. */
public class GetPositionsDTO extends AbstractGetPositionsDTO<PositionDTO>
{
    public GetPositionsDTO()
    {
        super();
    }

    public GetPositionsDTO(List<PositionDTO> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        super(positions, securities, openPositionsCount, closedPositionsCount);
    }

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
