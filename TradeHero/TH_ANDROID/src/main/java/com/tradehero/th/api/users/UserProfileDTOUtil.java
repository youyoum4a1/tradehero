package com.tradehero.th.api.users;

import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.utils.SecurityUtils;

/**
 * Created by xavier on 12/13/13.
 */
public class UserProfileDTOUtil
{
    public static final String TAG = UserProfileDTOUtil.class.getSimpleName();

    public static Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO)
    {
        return getMaxPurchasableShares(userProfileDTO, quoteDTO, true);
    }

    public static Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO, boolean includeTransactionCost)
    {
        if (quoteDTO == null || quoteDTO.ask == null || quoteDTO.ask == 0 || quoteDTO.toUSDRate == null || quoteDTO.toUSDRate == 0 ||
                userProfileDTO == null || userProfileDTO.portfolio == null)
        {
            return null;
        }
        return (int) Math.floor((userProfileDTO.portfolio.cashBalance - (includeTransactionCost ? SecurityUtils.DEFAULT_TRANSACTION_COST : 0)) / (quoteDTO.ask * quoteDTO.toUSDRate));
    }
}
