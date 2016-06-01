package com.ayondo.academy.api.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.security.compact.FxSecurityCompactDTO;
import com.ayondo.academy.api.security.key.FxPairSecurityId;
import com.ayondo.academy.fragments.base.DashboardFragment;
import com.ayondo.academy.fragments.trade.BuySellStockFragment;
import com.ayondo.academy.fragments.trade.FXMainFragment;
import com.ayondo.academy.models.number.THSignedNumber;
import java.text.DecimalFormatSymbols;

public class SecurityCompactDTOUtil
{
    public static final int DEFAULT_RELEVANT_DIGITS = 20;

    public static int getExpectedPrecision(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        return getExpectedPrecision(securityCompactDTO.askPrice, securityCompactDTO.bidPrice);
    }

    public static int getExpectedPrecision(double ask, double bid)
    {
        String askPrice = THSignedNumber.builder(ask)
                .relevantDigitCount(DEFAULT_RELEVANT_DIGITS)
                .build().toString();
        String bidPrice = THSignedNumber.builder(bid)
                .relevantDigitCount(DEFAULT_RELEVANT_DIGITS)
                .build().toString();
        char decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        int askDecimalPlace = askPrice.indexOf(decimalSeparator);
        int bidDecimalPlace = bidPrice.indexOf(decimalSeparator);

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

    @NonNull public static String getShortSymbol(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            FxPairSecurityId fxPairSecurityId = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
            return String.format("%s/%s", fxPairSecurityId.left, fxPairSecurityId.right);
        }

        SecurityId securityId = securityCompactDTO.getSecurityId();
        return String.format(
                "%s:%s",
                securityId.getExchange(),
                securityId.getSecuritySymbol());
    }

    @NonNull public static Class<? extends DashboardFragment> fragmentFor(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            return FXMainFragment.class;
        }
        else
        {
            return BuySellStockFragment.class;
        }
    }
}
