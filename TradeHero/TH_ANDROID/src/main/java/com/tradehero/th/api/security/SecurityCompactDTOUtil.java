package com.tradehero.th.api.security;

import android.support.annotation.NonNull;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

public class SecurityCompactDTOUtil
{
    public static final int DEFAULT_RELEVANT_DIGITS = 20;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactDTOUtil()
    {
        super();
    }
    //</editor-fold>

    public static int getExpectedPrecision(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        String askPrice = THSignedNumber.builder(securityCompactDTO.askPrice)
                .relevantDigitCount(DEFAULT_RELEVANT_DIGITS)
                .build().toString();
        String bidPrice = THSignedNumber.builder(securityCompactDTO.bidPrice)
                .relevantDigitCount(DEFAULT_RELEVANT_DIGITS)
                .build().toString();
        int askDecimalPlace = askPrice.indexOf('.');
        int bidDecimalPlace = bidPrice.indexOf('.');

        if (askDecimalPlace >= 0 && bidDecimalPlace >= 0)
        {
            int askDecimalCount = askPrice.length() - askDecimalPlace - 1;
            int bidDecimalCount = bidPrice.length() - bidDecimalPlace - 1;

            return Math.max(askDecimalCount, bidDecimalCount);
        }
        else if (askDecimalPlace >= 0)
        {
            return askDecimalPlace;
        }
        else if (bidDecimalPlace >= 0)
        {
            return bidDecimalPlace;
        }
        return 0;
    }
}
