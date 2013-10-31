package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by julien on 31/10/13
 */
public class PositionUtils
{
    public static String getSumInvested(Context context, PositionDTO position)
    {
        if (position != null && position.sumInvestedAmountRefCcy != null)
        {
            return NumberDisplayUtils.formatWithRelevantDigits(
                    position.sumInvestedAmountRefCcy,
                    4,
                    SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY);
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public static String getRealizedPL(Context context, PositionDTO position)
    {
        if (position != null && position.realizedPLRefCcy != null)
        {
            return NumberDisplayUtils.formatWithRelevantDigits(
                    position.realizedPLRefCcy,
                    4,
                    SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY);
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public static String getMarketValue(Context context, PositionDTO position)
    {
        if (position != null)
        {
            return NumberDisplayUtils.formatWithRelevantDigits(
                    position.marketValueRefCcy,
                    4,
                    SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY);
        }
        else
        {
            return context.getString(R.string.na);
        }
    }
}
