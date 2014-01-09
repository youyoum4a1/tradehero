package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Created by xavier on 12/13/13.
 */
public class SecurityPositionDetailDTOUtil
{
    public static final String TAG = SecurityPositionDetailDTOUtil.class.getSimpleName();

    public static Integer getMaxSellableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO, SecurityPositionDetailDTO securityPositionDetailDTO,
            PortfolioId portfolioId)
    {
        if (securityPositionDetailDTO != null && securityPositionDetailDTO.positions != null && portfolioId != null)
        {
            return securityPositionDetailDTO.positions.getMaxSellableShares(userProfileDTO, quoteDTO, portfolioId);
        }
        return null;
    }
}
