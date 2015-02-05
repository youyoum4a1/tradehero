package com.tradehero.th.models.position;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.THColorUtils;

public class PositionDTOUtils
{
    @NonNull public static String getSumInvested(@NonNull Resources resources, PositionDTO position)
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

    @NonNull private static String getSumInvested(@NonNull Resources resources,
            @Nullable PositionDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.sumInvestedAmountRefCcy != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.sumInvestedAmountRefCcy)
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

    @NonNull public static String getValueAtStart(@NonNull Resources resources, @Nullable PositionInPeriodDTO position)
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

    @NonNull private static String getValueAtStart(
            @NonNull Resources resources,
            @Nullable PositionInPeriodDTO position,
            @Nullable String refCurrency)
    {
        if (position != null &&
                position.marketValueStartPeriodRefCcy != null &&
                /* It appears iOS version does that */position.marketValueStartPeriodRefCcy > 0)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.marketValueStartPeriodRefCcy)
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

    public static void setRealizedPLLook(@NonNull TextView textView, @NonNull PositionDTO positionDTO)
    {
        textView.setText(getRealizedPL(textView.getResources(), positionDTO));
        if (positionDTO.realizedPLRefCcy != null)
        {
            textView.setTextColor(textView.getResources().getColor(THColorUtils.getColorResourceIdForNumber(positionDTO.realizedPLRefCcy)));
        }
        else
        {
            textView.setTextColor(textView.getResources().getColor(R.color.black));
        }
    }

    @NonNull private static String getRealizedPL(
            @NonNull Resources resources,
            @Nullable PositionDTO position)
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

    @NonNull private static String getRealizedPL(
            @NonNull Resources resources,
            @Nullable PositionDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.realizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.realizedPLRefCcy)
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

    @NonNull public static String getInPeriodRealizedPL(
            @NonNull Resources resources,
            @Nullable PositionInPeriodDTO position)
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

    @NonNull private static String getInPeriodRealizedPL(
            @NonNull Resources resources,
            @Nullable PositionInPeriodDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.totalPLInPeriodRefCcy != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.totalPLInPeriodRefCcy)
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

    @NonNull public static String getMarketValue(
            @NonNull Resources resources,
            @Nullable PositionDTO position)
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

    @NonNull private static String getMarketValue(
            @NonNull Resources resources,
            @Nullable PositionDTO position,
            @Nullable String refCurrency)
    {
        if (position != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.marketValueRefCcy)
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

    public static void setUnrealizedPLLook(
            @NonNull TextView textView,
            @NonNull PositionDTO positionDTO)
    {
        textView.setText(getUnrealizedPL(textView.getResources(), positionDTO));
        if (positionDTO.unrealizedPLRefCcy != null)
        {
            textView.setTextColor(textView.getResources().getColor(THColorUtils.getColorResourceIdForNumber(positionDTO.unrealizedPLRefCcy)));
        }
        else
        {
            textView.setTextColor(textView.getResources().getColor(R.color.black));
        }
    }

    @NonNull private static String getUnrealizedPL(
            @NonNull Resources resources,
            @Nullable PositionDTO position)
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

    @NonNull private static String getUnrealizedPL(
            @NonNull Resources resources,
            @Nullable PositionDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.unrealizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.unrealizedPLRefCcy)
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

    public static void setROISinceInception(
            @NonNull TextView textView,
            @Nullable PositionDTO positionDTO)
    {
        if (positionDTO != null)
        {
            setROILook(textView, positionDTO.getROISinceInception());
        }
    }

    public static void setROIInPeriod(
            @NonNull TextView textView,
            @Nullable PositionInPeriodDTO positionInPeriodDTO)
    {
        if (positionInPeriodDTO != null)
        {
            setROILook(textView, positionInPeriodDTO.getROIInPeriod());
        }
    }

    static void setROILook(@NonNull TextView textView, @Nullable Double roiValue)
    {
        if (roiValue == null)
        {
            textView.setText(R.string.na);
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.black));
        }
        else
        {
            THSignedPercentage.builder(roiValue * 100.0)
                    .signTypeArrow()
                    .withDefaultColor()
                    .relevantDigitCount(3)
                    .build()
                    .into(textView);
        }
    }

    @NonNull public static String getAdditionalInvested(
            @NonNull Resources resources,
            @Nullable PositionInPeriodDTO position)
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

    @NonNull private static String getAdditionalInvested(
            @NonNull Resources resources,
            @Nullable PositionInPeriodDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.sum_purchasesInPeriodRefCcy != null)
        {

            THSignedNumber formatSumPurchasesInPeriodRefCcy = THSignedMoney.builder(position.sum_purchasesInPeriodRefCcy)
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
