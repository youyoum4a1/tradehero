package com.tradehero.th.utils;

import android.content.Context;
import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 4:45 PM To change this template use File | Settings | File Templates. */
public class NumberDisplayUtils
{
    public static final int[] SUFFIX_IDS =
    {
        R.string.number_presentation_unit_suffix,
        R.string.number_presentation_thousand_suffix,
        R.string.number_presentation_million_suffix,
        R.string.number_presentation_billion_suffix,
        R.string.number_presentation_trillion_suffix
    };

    // These suffixes are used in tests...
    private static final String[] FALLBACK_SUFFIXES = {"", "k", "M", "B", "Tr"};

    @Inject static public Context context;

    public static int getPlusMinusPrefixResId(double value)
    {
        return value > 0 ? R.string.sign_prefix_positive :
                value < 0 ? R.string.sign_prefix_negative :
                        R.string.sign_prefix_zero;
    }

    public static String getPlusMinusPrefix(double value)
    {
        return Application.getResourceString(getPlusMinusPrefixResId(value));
    }

    public static int getMinusOnlyPrefixResId(double value)
    {
        return value < 0 ? R.string.sign_prefix_negative : R.string.sign_prefix_zero;
    }

    public static String getMinusOnlyPrefix(double value)
    {
        return Application.getResourceString(getMinusOnlyPrefixResId(value));
    }

    public static int getArrowPrefixResId(double value)
    {
        return value > 0 ? R.string.arrow_prefix_positive :
                value < 0 ? R.string.arrow_prefix_negative :
                R.string.arrow_prefix_zero;
    }

    public static String getArrowPrefix(double value)
    {
        return Application.getResourceString(getArrowPrefixResId(value));
    }

    public static String formatWithRelevantDigits(double number, int relevantDigits)
    {
        return formatWithRelevantDigits(number, relevantDigits, null);
    }

    public static String formatWithRelevantDigits(double number, int relevantDigits, String prefix)
    {
        double absVal = Math.abs(number);
        if (absVal > 999999999999999d)
        {
            throw new IllegalArgumentException("We do not accept number larger than 999 trillions");
        }

        if (absVal == 0)
        {
            return "0";
        }

        // Pick suffix
        int tensCategory = (int) Math.floor(Math.log10(absVal));
        int redressDigits = 0;
        if (relevantDigits > 3 && tensCategory >= 3)
        {
            // To change something like 2.592k into 2,592 or something like 2.592M into 2,592k
            redressDigits = relevantDigits - (relevantDigits % 3);
        }
        int suffixIndex = Math.max(0, (int) Math.floor(((float) (tensCategory - redressDigits)) / 3));

        // Get number reduced by suffix
        double reducedNumber = number / Math.pow(10, suffixIndex * 3);

        int desiredDecimal = 2;

        if (absVal >= 1)
        {
            // Number of desired decimals after the decimal separator
            int reducedDigitCount = 1 + tensCategory - suffixIndex * 3;
            desiredDecimal = Math.max(0, relevantDigits - reducedDigitCount);
        }

        String suffix;
        try
        {
            suffix = context.getResources().getString(SUFFIX_IDS[suffixIndex]);
        }
        catch (Resources.NotFoundException e)
        {
            suffix = FALLBACK_SUFFIXES[suffixIndex];
        }

        if (prefix != null)
        {
            return String.format("%s%,." + desiredDecimal + "f%s", prefix, reducedNumber, suffix);
        }
        else
        {
            return String.format("%,." + desiredDecimal + "f%s", reducedNumber, suffix);
        }
    }
}
