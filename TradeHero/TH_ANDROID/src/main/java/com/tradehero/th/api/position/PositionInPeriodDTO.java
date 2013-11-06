package com.tradehero.th.api.position;

import com.tradehero.th.api.leaderboard.position.LeaderbordMarkUserPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:11 PM To change this template use File | Settings | File Templates. */
public class PositionInPeriodDTO extends PositionDTO
{
    // This leaderboard Mark User Id needs to be populated by the service
    private Integer leaderboardMarkUserId;

    public Double totalPLInPeriodRefCcy;
    public Double marketValueStartPeriodRefCcy;
    public Double marketValueEndPeriodRefCcy;
    public Double sum_salesInPeriodRefCcy;
    public Double sum_purchasesInPeriodRefCcy;

    public LeaderbordMarkUserPositionId getLbPositionId()
    {
        return new LeaderbordMarkUserPositionId(id);
    }

    public OwnedLeaderboardPositionId getLbOwnedPositionId()
    {
        return new OwnedLeaderboardPositionId(leaderboardMarkUserId, id);
    }

    public Integer getLeaderboardMarkUserId()
    {
        return leaderboardMarkUserId;
    }

    public void setLeaderboardMarkUserId(Integer leaderboardMarkUserId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
    }

    public static List<OwnedLeaderboardPositionId> getFiledLbPositionIds(List<PositionInPeriodDTO> positionInPeriodDTOs)
    {
        if (positionInPeriodDTOs == null)
        {
            return null;
        }

        List<OwnedLeaderboardPositionId> ownedPositionIds = new ArrayList<>();

        for (PositionInPeriodDTO positionInPeriodDTO: positionInPeriodDTOs)
        {
            ownedPositionIds.add(positionInPeriodDTO.getLbOwnedPositionId());
        }

        return ownedPositionIds;
    }
}
