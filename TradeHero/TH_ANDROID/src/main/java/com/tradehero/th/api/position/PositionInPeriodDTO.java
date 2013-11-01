package com.tradehero.th.api.position;

import com.tradehero.th.api.leaderboard.position.LbPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLbPositionId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:11 PM To change this template use File | Settings | File Templates. */
public class PositionInPeriodDTO extends PositionDTO
{
    public Double totalPLInPeriodRefCcy;
    public Double marketValueStartPeriodRefCcy;
    public Double marketValueEndPeriodRefCcy;
    public Double sum_salesInPeriodRefCcy;
    public Double sum_purchasesInPeriodRefCcy;

    public LbPositionId getLbPositionId()
    {
        return new LbPositionId(id);
    }

    public OwnedLbPositionId getLbOwnedPositionId(Integer lbmuId)
    {
        return new OwnedLbPositionId(lbmuId,  id);
    }
}
