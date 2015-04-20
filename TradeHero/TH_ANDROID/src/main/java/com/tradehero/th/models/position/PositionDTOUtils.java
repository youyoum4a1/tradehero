package com.tradehero.th.models.position;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Pair;
import android.widget.TextView;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.THColorUtils;
import rx.Observable;
import rx.functions.Func1;

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

    //<editor-fold desc="Realised PL">
    public static void setRealizedPLLook(@NonNull TextView textView, @NonNull PositionDTO positionDTO)
    {
        textView.setText(getRealisedPLSpanned(textView.getResources(), positionDTO));
    }

    @NonNull public static Spanned getRealisedPLSpanned(@NonNull Resources resources,
            @Nullable PositionDTO position)
    {
        if (position != null)
        {
            return getRealisedPLSpanned(resources, position, position.getNiceCurrency());
        }
        return new SpannableString(resources.getString(R.string.na));
    }

    @NonNull public static Spanned getRealisedPLSpanned(@NonNull Resources resources,
            @Nullable PositionDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.realizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.realizedPLRefCcy)
                    .withOutSign()
                    .withValueColor(resources.getColor(THColorUtils.getColorResourceIdForNumber(position.realizedPLRefCcy)))
                    .currency(refCurrency)
                    .build();
            return formattedNumber.createSpanned();
        }
        return new SpannableString(resources.getString(R.string.na));
    }
    //</editor-fold>

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

    //<editor-fold desc="Unrealised">
    public static void setUnrealizedPLLook(
            @NonNull TextView textView,
            @NonNull PositionDTO positionDTO)
    {
        textView.setText(getUnrealisedPLSpanned(textView.getResources(), positionDTO));
    }

    @NonNull public static Spanned getUnrealisedPLSpanned(@NonNull Resources resources,
            @Nullable PositionDTO position)
    {
        if (position != null)
        {
            return getUnrealisedPLSpanned(resources, position, position.getNiceCurrency());
        }
        return new SpannableString(resources.getString(R.string.na));
    }

    @NonNull public static Spanned getUnrealisedPLSpanned(@NonNull Resources resources,
            @Nullable PositionDTO position,
            @Nullable String refCurrency)
    {
        if (position != null && position.unrealizedPLRefCcy != null)
        {
            THSignedNumber formattedNumber = THSignedMoney.builder(position.unrealizedPLRefCcy)
                    .withOutSign()
                    .currency(refCurrency)
                    .withValueColor(resources.getColor(THColorUtils.getColorResourceIdForNumber(position.unrealizedPLRefCcy)))
                    .build();
            return formattedNumber.createSpanned();
        }
        return new SpannableString(resources.getString(R.string.na));
    }
    //</editor-fold>

    //<editor-fold desc="ROI">
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
        textView.setText(getROISpanned(textView.getResources(), roiValue));
    }

    public static Spanned getROISpanned(@NonNull Resources resources, @Nullable Double roiValue)
    {
        if (roiValue == null)
        {
            return new SpannableString(resources.getString(R.string.na));
        }
        else
        {
            return THSignedPercentage.builder(roiValue * 100.0)
                    .signTypePlusMinusAlways()
                    .withDefaultColor()
                    .relevantDigitCount(3)
                    .build()
                    .createSpanned();
        }
    }
    //</editor-fold>

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

    @NonNull public static Observable<Pair<PositionDTO, SecurityCompactDTO>> getSecuritiesSoft(
            @NonNull Observable<PositionDTO> positionDTOs,
            @NonNull final BaseDTOCacheRx<SecurityIntegerId, SecurityId> securityIdCache,
            @NonNull final BaseDTOCacheRx<SecurityId, SecurityCompactDTO> securityCompactCache)
    {
        return getSecuritiesSoft(
                getSecurityIdsSoft(positionDTOs, securityIdCache),
                securityCompactCache);
    }

    @NonNull public static Observable<Pair<PositionDTO, SecurityId>> getSecurityIdsSoft(
            @NonNull Observable<PositionDTO> positionDTOs,
            @NonNull final BaseDTOCacheRx<SecurityIntegerId, SecurityId> securityIdCache)
    {
        return positionDTOs.flatMap(
                new Func1<PositionDTO, Observable<Pair<PositionDTO, SecurityId>>>()
                {
                    @Override public Observable<Pair<PositionDTO, SecurityId>> call(final PositionDTO positionDTO)
                    {
                        if (positionDTO.securityId <= 0)
                        {
                            return Observable.just(Pair.create(positionDTO, (SecurityId) null));
                        }
                        return securityIdCache.getOne(positionDTO.getSecurityIntegerId())
                                .map(
                                        new Func1<Pair<SecurityIntegerId, SecurityId>, Pair<PositionDTO, SecurityId>>()
                                        {
                                            @Override public Pair<PositionDTO, SecurityId> call(Pair<SecurityIntegerId, SecurityId> idPair)
                                            {
                                                return Pair.create(positionDTO, idPair.second);
                                            }
                                        });
                    }
                });
    }

    @NonNull public static <PositionType extends PositionDTOCompact> Observable<Pair<PositionType, SecurityCompactDTO>> getSecuritiesSoft(
            @NonNull Observable<Pair<PositionType, SecurityId>> securityIds,
            @NonNull final BaseDTOCacheRx<SecurityId, SecurityCompactDTO> securityCompactCache)
    {
        return securityIds.flatMap(
                new Func1<Pair<PositionType, SecurityId>, Observable<Pair<PositionType, SecurityCompactDTO>>>()
                {
                    @Override public Observable<Pair<PositionType, SecurityCompactDTO>> call(final Pair<PositionType, SecurityId> positionPair)
                    {
                        if (positionPair.second == null)
                        {
                            return Observable.just(Pair.create(positionPair.first, (SecurityCompactDTO) null));
                        }
                        return securityCompactCache.getOne(positionPair.second)
                                .map(
                                        new Func1<Pair<SecurityId, SecurityCompactDTO>, Pair<PositionType, SecurityCompactDTO>>()
                                        {
                                            @Override
                                            public Pair<PositionType, SecurityCompactDTO> call(
                                                    Pair<SecurityId, SecurityCompactDTO> securityCompactPair)
                                            {
                                                return Pair.create(positionPair.first, securityCompactPair.second);
                                            }
                                        });
                    }
                });
    }
}
