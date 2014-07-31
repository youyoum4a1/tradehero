package com.tradehero.th.models.position;

import android.content.res.Resources;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

public class PositionDTOUtils
{
    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    @Inject public PositionDTOUtils()
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
            THSignedNumber formattedNumber = THSignedMoney.builder()
                    .number(position.sumInvestedAmountRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
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
            THSignedNumber formattedNumber = THSignedMoney.builder()
                    .number(position.marketValueStartPeriodRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
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
            textView.setTextColor(textView.getResources().getColor(ColorUtils.getColorResourceIdForNumber(positionDTO.realizedPLRefCcy)));
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
            THSignedNumber formattedNumber = THSignedMoney.builder()
                    .number(position.realizedPLRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
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
            THSignedNumber formattedNumber = THSignedMoney.builder()
                    .number(position.totalPLInPeriodRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
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
            THSignedNumber formattedNumber = THSignedMoney.builder()
                    .number(position.marketValueRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
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
            textView.setTextColor(textView.getResources().getColor(ColorUtils.getColorResourceIdForNumber(positionDTO.unrealizedPLRefCcy)));
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
            THSignedNumber formattedNumber = THSignedMoney.builder()
                    .number(position.unrealizedPLRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
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
            THSignedNumber roiNumber = THSignedPercentage.builder()
                    .number(Math.abs(roiValue * 100.0))
                    .withSign()
                    .signTypeArrow()
                    .relevantDigitCount(3)
                    .build();
            textView.setText(roiNumber.toString());
            textView.setTextColor(
                    ColorUtils.getProperColorForNumber(roiValue.floatValue() * PERCENT_STRETCHING_FOR_COLOR));
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

            THSignedNumber formatSumPurchasesInPeriodRefCcy = THSignedMoney.builder()
                    .number(position.sum_purchasesInPeriodRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .build();
            return formatSumPurchasesInPeriodRefCcy.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }
}
