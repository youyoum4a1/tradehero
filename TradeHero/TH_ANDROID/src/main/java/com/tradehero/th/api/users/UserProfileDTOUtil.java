package com.tradehero.th.api.users;

import com.tradehero.th.api.quote.QuoteDTO;

/**
 * Created by xavier on 12/13/13.
 */
public class UserProfileDTOUtil
{
    public static final String TAG = UserProfileDTOUtil.class.getSimpleName();

    public static Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO)
    {
        if (quoteDTO == null || quoteDTO.ask == null || quoteDTO.ask == 0 || quoteDTO.toUSDRate == null || quoteDTO.toUSDRate == 0 ||
                userProfileDTO == null || userProfileDTO.portfolio == null)
        {
            return null;
        }
        return (int) Math.floor(userProfileDTO.portfolio.cashBalance / (quoteDTO.ask * quoteDTO.toUSDRate));
    }
}
