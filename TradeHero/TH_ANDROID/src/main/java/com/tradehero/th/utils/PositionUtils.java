package com.tradehero.th.utils;

import android.content.Context;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import javax.inject.Inject;


public class PositionUtils
{
    public static final String TAG = PositionUtils.class.getSimpleName();

    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    @Inject public PositionUtils()
    {
        super();
    }

    public String getSumInvested(Context context, PositionDTO position)
    {
        if (position != null)
        {
            return getSumInvested(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getSumInvested(Context context, PositionDTO position, String refCurrency)
    {
        if (position != null && position.sumInvestedAmountRefCcy != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.sumInvestedAmountRefCcy, false, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public String getValueAtStart(Context context, PositionInPeriodDTO position)
    {
        if (position != null)
        {
            return getValueAtStart(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getValueAtStart(Context context, PositionInPeriodDTO position, String refCurrency)
    {
        if (position != null &&
                position.marketValueStartPeriodRefCcy != null &&
                /* It appears iOS version does that */position.marketValueStartPeriodRefCcy > 0)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.marketValueStartPeriodRefCcy, false, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public String getRealizedPL(Context context, PositionDTO position)
    {
        if (position != null)
        {
            return getRealizedPL(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getRealizedPL(Context context, PositionDTO position, String refCurrency)
    {
        if (position != null && position.realizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    position.realizedPLRefCcy,
                    true,
                    refCurrency,
                    THSignedNumber.TYPE_SIGN_MINUS_ONLY);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public String getInPeriodRealizedPL(Context context, PositionInPeriodDTO position)
    {
        if (position != null)
        {
            return getInPeriodRealizedPL(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getInPeriodRealizedPL(Context context, PositionInPeriodDTO position, String refCurrency)
    {
        if (position != null && position.totalPLInPeriodRefCcy != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.totalPLInPeriodRefCcy, false, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public String getMarketValue(Context context, PositionDTO position)
    {
        if (position != null)
        {
            return getMarketValue(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getMarketValue(Context context, PositionDTO position, String refCurrency)
    {
        if (position != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.marketValueRefCcy, false, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public String getUnrealizedPL(Context context, PositionDTO position)
    {
        if (position != null)
        {
            return getUnrealizedPL(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getUnrealizedPL(Context context, PositionDTO position, String refCurrency)
    {
        if (position != null && position.unrealizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    position.unrealizedPLRefCcy,
                    true,
                    refCurrency,
                    THSignedNumber.TYPE_SIGN_MINUS_ONLY);
            return formattedNumber.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    public void setROISinceInception(TextView textView, PositionDTO positionDTO)
    {
        if (positionDTO != null)
        {
            setROILook(textView, positionDTO.getROISinceInception());
        }
    }

    public void setROIInPeriod(TextView textView, PositionInPeriodDTO positionInPeriodDTO)
    {
        if (positionInPeriodDTO != null)
        {
            setROILook(textView, positionInPeriodDTO.getROIInPeriod());
        }
    }

    private void setROILook(TextView textView, Double roiValue)
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

    public String getAdditionalInvested(Context context, PositionInPeriodDTO position)
    {
        if (position != null)
        {
            return getAdditionalInvested(context, position, position.getNiceCurrency());
        }
        else
        {
            return context.getString(R.string.na);
        }
    }

    private String getAdditionalInvested(Context context, PositionInPeriodDTO position, String refCurrency)
    {
        if (position != null && position.sum_purchasesInPeriodRefCcy != null)
        {

            THSignedNumber formatSumPurchasesInPeriodRefCcy =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.sum_purchasesInPeriodRefCcy, false, refCurrency);
            return formatSumPurchasesInPeriodRefCcy.toString();
        }
        else
        {
            return context.getString(R.string.na);
        }
    }
}
