package com.tradehero.th.api.position;

import com.tradehero.th.api.security.SecurityCompactDTOList;

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
}
