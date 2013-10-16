package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:35 PM To change this template use File | Settings | File Templates. */
public class GetPositionsDTO
{
    public List<PositionDTO> positions;
    public List<SecurityCompactDTO> securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    public GetPositionsDTO(List<PositionDTO> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        this.positions = positions;
        this.securities = securities;
        this.openPositionsCount = openPositionsCount;
        this.closedPositionsCount = closedPositionsCount;
    }

    public List<FiledPositionId> getFiledPositionIds(PortfolioId portfolioId)
    {
        if (positions == null)
        {
            return null;
        }

        List<FiledPositionId> filedPositionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positions)
        {
            filedPositionIds.add(new FiledPositionId(positionDTO.userId, positionDTO.securityId, portfolioId.key));
        }

        return filedPositionIds;
    }
}
