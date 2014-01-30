package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.SecurityUtils;

/**
 * Created by xavier on 12/13/13.
 */
public class SecurityPositionDetailDTOUtil
{
    public static final String TAG = SecurityPositionDetailDTOUtil.class.getSimpleName();

    public static Integer getMaxSellableShares(SecurityPositionDetailDTO securityPositionDetailDTO, QuoteDTO quoteDTO, PortfolioId portfolioId,
            UserProfileDTO userProfileDTO)
    {
        if (securityPositionDetailDTO != null && securityPositionDetailDTO.positions != null && portfolioId != null)
        {
            return securityPositionDetailDTO.positions.getMaxSellableShares(quoteDTO, portfolioId, userProfileDTO);
        }
        return null;
    }

    public static Double projectedPLValue(SecurityPositionDetailDTO securityPositionDetailDTO, QuoteDTO quoteDTO, Integer shareQuantity)
    {
        return projectedPLValue(securityPositionDetailDTO, quoteDTO, shareQuantity, true);
    }

    public static Double projectedPLValue(SecurityPositionDetailDTO securityPositionDetailDTO, QuoteDTO quoteDTO, Integer shareQuantity, boolean includeTransactionCost)
    {
        if (shareQuantity != null &&
                securityPositionDetailDTO != null &&
                securityPositionDetailDTO.positions != null &&
                securityPositionDetailDTO.positions.get(0).averagePriceRefCcy != null &&
                quoteDTO != null &&
                quoteDTO.bid != null &&
                quoteDTO.toUSDRate != null)
        {
            double buyPrice = shareQuantity * securityPositionDetailDTO.positions.get(0).averagePriceRefCcy;
            double sellPrice = shareQuantity * quoteDTO.bid * quoteDTO.toUSDRate;
            double plValue = sellPrice - buyPrice;
            if (shareQuantity > 0 && includeTransactionCost)
            {
                plValue -= SecurityUtils.DEFAULT_TRANSACTION_COST;
            }
            return plValue;
        }
        else
        {
            return null;
        }
    }
}
