package com.tradehero.th.api.leaderboard.position;

import com.tradehero.th.api.position.AbstractGetPositionsDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

public class GetLeaderboardPositionsDTO extends AbstractGetPositionsDTO<PositionInPeriodDTO>
{
    //<editor-fold desc="Constructors">
    public GetLeaderboardPositionsDTO()
    {
        super();
    }

    public GetLeaderboardPositionsDTO(PositionDTOList<PositionInPeriodDTO> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        super(positions, securities, openPositionsCount, closedPositionsCount);
    }
    //</editor-fold>

    public List<OwnedLeaderboardPositionId> getFiledPositionIds()
    {
        if (positions == null)
        {
            return null;
        }

        List<OwnedLeaderboardPositionId> ownedLeaderboardPositionIds = new ArrayList<>();

        for (PositionInPeriodDTO positionInPeriodDTO : positions)
        {
            ownedLeaderboardPositionIds.add(positionInPeriodDTO.getLbOwnedPositionId());
        }

        return ownedLeaderboardPositionIds;
    }
}

