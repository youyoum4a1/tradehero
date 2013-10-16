package com.tradehero.th.api.position;

import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:35 PM To change this template use File | Settings | File Templates. */
public class GetPositionsDTO
{
    public List<PositionDTO> positions ;
    public List<SecurityCompactDTO> securities;
    public int openPositionsCount;
    public int closedPositionsCount;
}
