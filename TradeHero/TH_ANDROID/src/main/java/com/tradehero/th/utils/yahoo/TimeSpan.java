package com.tradehero.th.utils.yahoo;

import com.tradehero.th.R;

/**
 * Created by julien on 9/10/13
 */
public enum TimeSpan
{
    day1("1d", R.string.yahoo_chart_1d),
    day5("5d", R.string.yahoo_chart_5d),
    month3("3m", R.string.yahoo_chart_3m),
    month6("6m", R.string.yahoo_chart_6m),
    year1("1y", R.string.yahoo_chart_1y),
    year2("2y", R.string.yahoo_chart_2y),
    year5("5y", R.string.yahoo_chart_5y),
    yearMax("my", R.string.yahoo_chart_max);

    public final String code;
    public final int stringResId;

    private TimeSpan(String c, int stringResId)
    {
        code = c;
        this.stringResId = stringResId;
    }

    public boolean equalsCode(String otherCode)
    {
        return (otherCode != null) && otherCode.equals(code);
    }

    public String toString()
    {
        return code;
    }
}