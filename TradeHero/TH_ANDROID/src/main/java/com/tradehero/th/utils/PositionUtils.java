package com.tradehero.th.utils;

import android.content.Context;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.base.Application;

/**
 * Created by julien on 31/10/13
 */
public class PositionUtils
{
    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

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

    public static String getValueAtStart(Context context, PositionInPeriodDTO position)
    {
        if (position != null && position.marketValueStartPeriodRefCcy != null)
        {
            return NumberDisplayUtils.formatWithRelevantDigits(
                    position.marketValueStartPeriodRefCcy,
                    7,
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

    public static String getInPeriodRealizedPL(Context context, PositionInPeriodDTO position)
    {
        if (position != null && position.totalPLInPeriodRefCcy != null)
        {
            return NumberDisplayUtils.formatWithRelevantDigits(
                    position.totalPLInPeriodRefCcy,
                    7,
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

    public static String getUnrealizedPL(Context context, PositionDTO position)
    {
        if (position != null && position.unrealizedPLRefCcy != null)
        {
            return (NumberDisplayUtils.formatWithRelevantDigits(
                    position.unrealizedPLRefCcy,
                    4,
                    SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public static void setROISinceInception(TextView textView, PositionDTO positionDTO)
    {
        if (positionDTO != null)
        {
            setROILook(textView, positionDTO.getROISinceInception());
        }
    }

    public static void setROIInPeriod(TextView textView, PositionInPeriodDTO positionInPeriodDTO)
    {

        if (positionInPeriodDTO != null)
        {
            setROILook(textView, positionInPeriodDTO.getROIInPeriod());
        }
    }

    private static void setROILook(TextView textView, Double roiValue)
    {
        if (roiValue == null)
        {
            textView.setText(R.string.na);
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.black));
        }
        else
        {
            String roiText = NumberDisplayUtils.getArrowPrefix(roiValue);
            roiText += NumberDisplayUtils.formatWithRelevantDigits(Math.abs(roiValue * 100.0), 3) + "%";
            textView.setText(roiText);
            textView.setTextColor(
                    ColorUtils.getColorForPercentage(roiValue.floatValue() * PERCENT_STRETCHING_FOR_COLOR));
        }
    }

    public static String getAdditionalInvested(Context context, PositionInPeriodDTO position)
    {
        if (position != null && position.sum_purchasesInPeriodRefCcy != null)
        {
            return NumberDisplayUtils.formatWithRelevantDigits(
                    position.sum_purchasesInPeriodRefCcy,
                    2,
                    SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY);
        }
        else
        {
            return context.getString(R.string.na);
        }
    }
}
