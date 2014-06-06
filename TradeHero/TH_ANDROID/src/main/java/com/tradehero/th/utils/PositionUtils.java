package com.tradehero.th.utils;

import android.content.res.Resources;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import javax.inject.Inject;

public class PositionUtils
{
    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    @Inject public PositionUtils()
    {
        super();
    }

    public String getSumInvested(Resources resources, PositionDTO position)
    {
        if (position != null)
        {
            return getSumInvested(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getSumInvested(Resources resources, PositionDTO position, String refCurrency)
    {
        if (position != null && position.sumInvestedAmountRefCcy != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.sumInvestedAmountRefCcy, THSignedNumber.WITHOUT_SIGN, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    public String getValueAtStart(Resources resources, PositionInPeriodDTO position)
    {
        if (position != null)
        {
            return getValueAtStart(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getValueAtStart(Resources resources, PositionInPeriodDTO position, String refCurrency)
    {
        if (position != null &&
                position.marketValueStartPeriodRefCcy != null &&
                /* It appears iOS version does that */position.marketValueStartPeriodRefCcy > 0)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.marketValueStartPeriodRefCcy, THSignedNumber.WITHOUT_SIGN, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    public void setRealizedPLLook(TextView textView, PositionDTO positionDTO)
    {
        textView.setText(getRealizedPL(textView.getResources(), positionDTO));
        if (positionDTO.realizedPLRefCcy != null)
        {
            textView.setTextColor(textView.getResources().getColor(ColorUtils.getColorResourceForNumber(positionDTO.realizedPLRefCcy)));
        }
        else
        {
            textView.setTextColor(textView.getResources().getColor(R.color.black));
        }
    }

    private String getRealizedPL(Resources resources, PositionDTO position)
    {
        if (position != null)
        {
            return getRealizedPL(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getRealizedPL(Resources resources, PositionDTO position, String refCurrency)
    {
        if (position != null && position.realizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    position.realizedPLRefCcy,
                    THSignedNumber.WITHOUT_SIGN,
                    refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    public String getInPeriodRealizedPL(Resources resources, PositionInPeriodDTO position)
    {
        if (position != null)
        {
            return getInPeriodRealizedPL(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getInPeriodRealizedPL(Resources resources, PositionInPeriodDTO position, String refCurrency)
    {
        if (position != null && position.totalPLInPeriodRefCcy != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.totalPLInPeriodRefCcy, THSignedNumber.WITHOUT_SIGN, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    public String getMarketValue(Resources resources, PositionDTO position)
    {
        if (position != null)
        {
            return getMarketValue(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getMarketValue(Resources resources, PositionDTO position, String refCurrency)
    {
        if (position != null)
        {
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.marketValueRefCcy, THSignedNumber.WITHOUT_SIGN, refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    public void setUnrealizedPLLook(TextView textView, PositionDTO positionDTO)
    {
        textView.setText(getUnrealizedPL(textView.getResources(), positionDTO));
        if (positionDTO.unrealizedPLRefCcy != null)
        {
            textView.setTextColor(textView.getResources().getColor(ColorUtils.getColorResourceForNumber(positionDTO.unrealizedPLRefCcy)));
        }
        else
        {
            textView.setTextColor(textView.getResources().getColor(R.color.black));
        }
    }

    private String getUnrealizedPL(Resources resources, PositionDTO position)
    {
        if (position != null)
        {
            return getUnrealizedPL(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getUnrealizedPL(Resources resources, PositionDTO position, String refCurrency)
    {
        if (position != null && position.unrealizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    position.unrealizedPLRefCcy,
                    THSignedNumber.WITHOUT_SIGN,
                    refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
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

    public String getAdditionalInvested(Resources resources, PositionInPeriodDTO position)
    {
        if (position != null)
        {
            return getAdditionalInvested(resources, position, position.getNiceCurrency());
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }

    private String getAdditionalInvested(Resources resources, PositionInPeriodDTO position, String refCurrency)
    {
        if (position != null && position.sum_purchasesInPeriodRefCcy != null)
        {

            THSignedNumber formatSumPurchasesInPeriodRefCcy =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, position.sum_purchasesInPeriodRefCcy, THSignedNumber.WITHOUT_SIGN, refCurrency);
            return formatSumPurchasesInPeriodRefCcy.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }
}
