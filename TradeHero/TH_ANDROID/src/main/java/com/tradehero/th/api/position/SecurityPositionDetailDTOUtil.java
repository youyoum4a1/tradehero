package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;

/**
 * Created by xavier on 12/13/13.
 */
public class SecurityPositionDetailDTOUtil
{
    public static final String TAG = SecurityPositionDetailDTOUtil.class.getSimpleName();

    public static Integer getMaxSellableShares(SecurityPositionDetailDTO securityPositionDetailDTO, PortfolioId portfolioId)
    {
        if (securityPositionDetailDTO != null && securityPositionDetailDTO.positions != null && portfolioId != null)
        {
            return securityPositionDetailDTO.positions.getMaxSellableShares(portfolioId);
        }
        return null;
    }
}
