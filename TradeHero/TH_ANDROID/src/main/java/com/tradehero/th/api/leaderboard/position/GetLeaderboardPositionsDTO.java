package com.tradehero.th.api.leaderboard.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.position.AbstractGetPositionsDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 1/11/13
 */
public class GetLeaderboardPositionsDTO extends AbstractGetPositionsDTO<PositionInPeriodDTO>
{
    public GetLeaderboardPositionsDTO()
    {
        super();
    }

    public GetLeaderboardPositionsDTO(List<PositionInPeriodDTO> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        super(positions, securities, openPositionsCount, closedPositionsCount);
    }

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

    @JsonIgnore public void setLeaderboardMarkUserId(LeaderboardMarkUserId leaderboardMarkUserId)
    {
        setLeaderboardMarkUserId(leaderboardMarkUserId.key);
    }

    @JsonIgnore public void setLeaderboardMarkUserId(Integer leaderboardMarkUserId)
    {
        if (positions == null)
        {
            return;
        }

        for (PositionInPeriodDTO positionInPeriodDTO : positions)
        {
            if (positionInPeriodDTO != null)
            {
                positionInPeriodDTO.setLeaderboardMarkUserId(leaderboardMarkUserId);
            }
        }
    }
}

