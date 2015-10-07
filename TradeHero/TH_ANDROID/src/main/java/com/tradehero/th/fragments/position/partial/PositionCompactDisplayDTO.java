package com.tradehero.th.fragments.position.partial;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;

public class PositionCompactDisplayDTO
{
    @NonNull public final PositionDTO positionDTO;
    @NonNull public final SecurityCompactDTO securityCompactDTO;
    @NonNull public final CharSequence shareCountText;
    @NonNull public final CharSequence positionGainLoss;
    public final CharSequence totalInvested;
    @NonNull public final CharSequence lastValue;
    @ViewVisibilityValue public final int shareCountVisibility;

    public PositionCompactDisplayDTO(
            Resources resources, @NonNull SecurityCompactDTO securityCompactDTO, @NonNull PositionDTO positionDTO)
    {
        String na = resources.getString(R.string.na);

        if ((positionDTO.positionStatus == PositionStatus.CLOSED
                || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED))
        {
            shareCountVisibility = View.GONE;
        }
        else
        {
            shareCountVisibility = View.VISIBLE;
        }
        this.securityCompactDTO = securityCompactDTO;
        this.positionDTO = positionDTO;

        @StringRes int sharesFormat = R.string.position_share_count_qty_suffix;
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            sharesFormat = R.string.position_fx_count_qty_suffix;
        }
        String buySell = positionDTO.isPending() ? (positionDTO.isShort() ? resources.getString(R.string.sell) : resources.getString(R.string.buy))
                : (positionDTO.isShort() ? resources.getString(R.string.sold) : resources.getString(R.string.bought));
        if (positionDTO.shares == null)
        {
            shareCountText = resources.getString(sharesFormat, buySell, resources.getString(R.string.na));
        }
        else
        {
            shareCountText = THSignedNumber.builder(Math.abs(positionDTO.shares))
                    .format(resources.getString(sharesFormat, buySell, "%1$s"))
                    .build()
                    .createSpanned();
        }

        final Double realisedPLRefCcy = positionDTO.realizedPLRefCcy;
        final Double unrealisedPLRefCcy = positionDTO.unrealizedPLRefCcy;

        //<editor-fold desc="Percent and Gain">
        final Double gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
                ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                : positionDTO.getROISinceInception();
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            Double PLR;
            if (positionDTO.positionStatus == PositionStatus.CLOSED
                    || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
            {
                PLR = realisedPLRefCcy;
            }
            else
            {
                PLR = unrealisedPLRefCcy;
            }
            if (PLR != null)
            {
                positionGainLoss =
                        THSignedMoney.builder(PLR)
                                .currency(positionDTO.getNiceCurrency())
                                .signTypePlusMinusAlways()
                                .withDefaultColor()
                                .build()
                                .createSpanned();
            }
            else
            {
                positionGainLoss = na;
            }
        }
        else
        {
            positionGainLoss = gainPercent == null
                    ? na
                    : THSignedPercentage.builder(gainPercent * 100.0)
                            .signTypeArrow()
                            .withSign()
                            .withDefaultColor()
                            .relevantDigitCount(3)
                            .build()
                            .createSpanned();
        }
        //</editor-fold>

        String totalInvestedFormat = resources.getString(R.string.position_invested);
        totalInvested = THSignedMoney.builder(positionDTO.sumInvestedAmountRefCcy)
                .currency(positionDTO.getNiceCurrency())
                .relevantDigitCount(3)
                .format(totalInvestedFormat)
                .build()
                .createSpanned();

        //<editor-fold desc="Last Amount">
        Boolean isOpen = positionDTO.isOpen();
        Boolean isClosed = positionDTO.isClosed();
        String lastAmountFormat = resources.getString(R.string.position_last_amount_header);
        if (isClosed != null && isClosed && realisedPLRefCcy != null)
        {
            lastValue = THSignedMoney.builder(realisedPLRefCcy)
                    .relevantDigitCount(3)
                    .withSign()
                    .signTypeMinusOnly()
                    .currency(positionDTO.getNiceCurrency())
                    .format(lastAmountFormat)
                    .build()
                    .createSpanned();
        }
        else if (isClosed != null && !isClosed)
        {
            lastValue = THSignedMoney.builder(positionDTO.marketValueRefCcy)
                    .relevantDigitCount(3)
                    .withSign()
                    .signTypeMinusOnly()
                    .currency(positionDTO.getNiceCurrency())
                    .format(lastAmountFormat)
                    .build()
                    .createSpanned();
        }
        else
        {
            lastValue = na;
        }
        //</editor-fold>

    }
}
