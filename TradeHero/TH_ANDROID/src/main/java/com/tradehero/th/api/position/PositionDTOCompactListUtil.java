package com.tradehero.th.api.position;

import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.util.List;
import javax.inject.Inject;

public class PositionDTOCompactListUtil
{
    @Inject public PositionDTOCompactListUtil()
    {
        super();
    }

    public Double projectedPLValue(List<PositionDTOCompact> positionDTOCompacts, QuoteDTO quoteDTO, Integer shareQuantity)
    {
        return projectedPLValue(positionDTOCompacts, quoteDTO, shareQuantity, true);
    }

    public Double projectedPLValue(List<PositionDTOCompact> positionDTOCompacts, QuoteDTO quoteDTO, Integer shareQuantity, boolean includeTransactionCost)
    {
        if (shareQuantity != null &&
                positionDTOCompacts != null &&
                positionDTOCompacts.get(0).averagePriceRefCcy != null &&
                quoteDTO != null &&
                quoteDTO.bid != null &&
                quoteDTO.toUSDRate != null)
        {
            double buyPrice = shareQuantity * positionDTOCompacts.get(0).averagePriceRefCcy;
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
