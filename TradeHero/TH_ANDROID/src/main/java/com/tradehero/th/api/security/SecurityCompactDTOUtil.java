package com.tradehero.th.api.security;

import android.support.annotation.NonNull;
import android.util.Pair;
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

    @NonNull public Pair<String, String> getFormattedAndPaddedAskBid(@NonNull SecurityCompactDTO securityCompactDTO)
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

            if (askDecimalCount != bidDecimalCount)
            {
                int targetDecimalCount = Math.max(askDecimalCount, bidDecimalCount);
                while(askDecimalCount < targetDecimalCount)
                {
                    askPrice += "0";
                    askDecimalCount++;
                }
                while(bidDecimalCount < targetDecimalCount)
                {
                    bidPrice += "0";
                    bidDecimalCount++;
                }
            }
        }
        else if (askDecimalPlace >= 0)
        {
            int askDecimalCount = askPrice.length() - askDecimalPlace - 1;
            int bidDecimalCount = 0;
            bidPrice += ".";
            while(bidDecimalCount < askDecimalCount)
            {
                bidPrice += "0";
                bidDecimalCount++;
            }
        }
        else if (bidDecimalPlace >= 0)
        {
            int bidDecimalCount = bidPrice.length() - bidDecimalPlace - 1;
            int askDecimalCount = 0;
            askPrice += ".";
            while(askDecimalCount < bidDecimalCount)
            {
                askPrice += "0";
                askDecimalCount++;
            }
        }

        return Pair.create(askPrice, bidPrice);
    }
}
