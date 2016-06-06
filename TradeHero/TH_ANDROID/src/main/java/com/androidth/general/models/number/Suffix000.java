package com.androidth.general.models.number;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.androidth.general.R;

public class Suffix000
{
    public final long divisor;
    @StringRes public final int suffixRes;
    @StringRes public final int suffixResLong;

    public Suffix000(long divisor, @StringRes int suffixRes, @StringRes int suffixResLong)
    {
        this.divisor = divisor;
        this.suffixRes = suffixRes;
        this.suffixResLong = suffixResLong;
    }

    @NonNull public static Suffix000 from(double number)
    {
        if (number >= 1000000000000d)
        {
            return new Suffix000(
                    1000000000000l,
                    R.string.number_presentation_trillion_suffix,
                    R.string.number_presentation_trillion_suffix_long);
        }
        else if (number >= 1000000000d)
        {
            return new Suffix000(
                    1000000000,
                    R.string.number_presentation_billion_suffix,
                    R.string.number_presentation_billion_suffix_long);
        }
        else if (number >= 1000000d)
        {
            return new Suffix000(
                    1000000,
                    R.string.number_presentation_million_suffix,
                    R.string.number_presentation_million_suffix_long);
        }
        else if (number >= 1000d)
        {
            return new Suffix000(
                    1000,
                    R.string.number_presentation_thousand_suffix,
                    R.string.number_presentation_thousand_suffix_long);
        }
        return new Suffix000(
                1,
                R.string.number_presentation_unit_suffix,
                R.string.number_presentation_unit_suffix_long);
    }
}
