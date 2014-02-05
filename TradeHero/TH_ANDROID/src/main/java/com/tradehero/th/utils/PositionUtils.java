package com.tradehero.th.utils;

import android.content.Context;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionDTO;

/**
 * Created by julien on 31/10/13
 */
public class PositionUtils
{
    public static final String TAG = PositionUtils.class.getSimpleName();

    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    public static String getSumInvested(Context context, PositionDTO position)
    {
        if (position != null && position.sumInvestedAmountRefCcy != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.sumInvestedAmountRefCcy, false);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public static String getValueAtStart(Context context, PositionInPeriodDTO position)
    {
        if (position != null &&
                position.marketValueStartPeriodRefCcy != null &&
                /* It appears iOS version does that */position.marketValueStartPeriodRefCcy > 0)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.marketValueStartPeriodRefCcy, false);
            return formattedNumber.toString();
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
            THSignedNumber formattedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    position.realizedPLRefCcy,
                    true,
                    SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                    THSignedNumber.TYPE_SIGN_MINUS_ONLY);
            return formattedNumber.toString();
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
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.totalPLInPeriodRefCcy, false);
            return formattedNumber.toString();
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
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.marketValueRefCcy, false);
            return formattedNumber.toString();
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
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.unrealizedPLRefCcy, false);
            return formattedNumber.toString();
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

            THSignedNumber formatSumPurchasesInPeriodRefCcy =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.sum_purchasesInPeriodRefCcy, false);
            return formatSumPurchasesInPeriodRefCcy.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }
}
