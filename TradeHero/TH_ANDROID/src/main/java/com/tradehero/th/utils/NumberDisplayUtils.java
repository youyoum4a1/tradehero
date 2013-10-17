package com.tradehero.th.utils;

import android.content.Context;
import android.content.res.Resources;
import com.tradehero.th.R;
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

    private static final String[] FALLBACK_SUFFIXES = {"", "k", "M", "B", "Tr"};

    @Inject static public Context context;

    public static String formatWithRelevantDigits(double number, int relevantDigits)
    {
        double absVal = Math.abs(number);
        if (absVal < 1 && absVal > 0)
        {
            throw new IllegalArgumentException("We do not accept number between 0 and 1");
        }
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
        int suffixIndex = (int) Math.floor(((float) tensCategory) / 3);
        String suffix;
        try
        {
            suffix = context.getResources().getString(SUFFIX_IDS[suffixIndex]);
        }
        catch (Resources.NotFoundException e)
        {
            suffix = FALLBACK_SUFFIXES[suffixIndex];
        }

        // Get number reduced by suffix
        double reducedNumber = number / Math.pow(10, suffixIndex * 3);

        // Number of desired decimals after the decimal separator
        int reducedDigitCount = 1 + tensCategory - suffixIndex * 3;
        int desiredDecimal = Math.max(0, relevantDigits - reducedDigitCount);

        return String.format("%,." + desiredDecimal + "f%s", reducedNumber, suffix);
    }
}
