package com.tradehero.th.fragments.position.partial;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;

public class PositionDisplayDTO
{
    @NonNull public final PositionDTO positionDTO;
    @NonNull public final SecurityCompactDTO securityCompactDTO;

    @ViewVisibilityValue public final int stockLogoVisibility;
    @Nullable public final String stockLogoUrl;
    @DrawableRes public final int stockLogoRes;
    @Nullable public final FxPairSecurityId fxPair;
    @NonNull public final String stockSymbol;
    @ViewVisibilityValue public final int flagsContainerVisibility;
    @ViewVisibilityValue public final int btnCloseVisibility;
    @ViewVisibilityValue public final int companyNameVisibility;
    @NonNull public final String companyName;
    @NonNull public final CharSequence lastPriceAndRise;
    @ViewVisibilityValue public final int shareCountRowVisibility;
    @NonNull public final CharSequence shareCountHeader;
    @ViewVisibilityValue public final int shareCountVisibility;
    @NonNull public final CharSequence shareCount;
    @NonNull public final CharSequence shareCountText;
    @ViewVisibilityValue public final int lastAmountContainerVisibility;
    @ViewVisibilityValue public final int positionPercentVisibility;
    @NonNull public final CharSequence positionPercent;
    @DrawableRes public final int gainIndicator;
    @NonNull public final String gainLossHeader;
    @NonNull public final CharSequence gainLoss;
    @NonNull public final CharSequence gainLossPercent;
    public final int gainLossColor;
    public final CharSequence totalInvested;
    @ViewVisibilityValue public final int unrealisedPLVisibility;
    @NonNull public final CharSequence unrealisedPL;
    @ViewVisibilityValue public final int lastAmountHeaderVisibility;
    @NonNull public final CharSequence lastAmount;

    public PositionDisplayDTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId, @NonNull PositionDTO positionDTO,
            @NonNull SecurityCompactDTO securityCompactDTO)
    {
        this.positionDTO = positionDTO;
        this.securityCompactDTO = securityCompactDTO;

        String na = resources.getString(R.string.na);

        //<editor-fold desc="Stock Logo">
        if (securityCompactDTO.imageBlobUrl != null)
        {
            stockLogoVisibility = View.VISIBLE;
            flagsContainerVisibility = View.GONE;
            stockLogoUrl = securityCompactDTO.imageBlobUrl;
            stockLogoRes = R.drawable.default_image;
        }
        else if (securityCompactDTO instanceof FxSecurityCompactDTO) // TODO Improve and show flags?
        {
            stockLogoVisibility = View.GONE;
            flagsContainerVisibility = View.VISIBLE;
            stockLogoUrl = null;
            stockLogoRes = R.drawable.default_image;
        }
        else
        {
            stockLogoVisibility = View.VISIBLE;
            flagsContainerVisibility = View.GONE;
            stockLogoUrl = null;
            stockLogoRes = securityCompactDTO.getExchangeLogoId();
        }
        //</editor-fold>

        //<editor-fold desc="Symbol and FxPair">
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            fxPair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
            stockSymbol = String.format("%s/%s", fxPair.left, fxPair.right);
        }
        else
        {
            fxPair = null;
            stockSymbol = String.format("%s:%s", securityCompactDTO.exchange, securityCompactDTO.symbol);
        }
        //</editor-fold>

        //<editor-fold desc="Company Name">
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            companyNameVisibility = View.GONE;
            companyName = "";
        }
        else
        {
            companyNameVisibility = View.VISIBLE;
            companyName = securityCompactDTO.name;
        }
        //</editor-fold>

        //<editor-fold desc="Last Price and Rise">
        final CharSequence lastPrice;
        if (securityCompactDTO.lastPrice != null)
        {
            lastPrice = THSignedMoney.builder(securityCompactDTO.lastPrice)
                    .relevantDigitCount(3)
                    .currency(securityCompactDTO.currencyDisplay)
                    .build()
                    .createSpanned();
        }
        else
        {
            lastPrice = resources.getString(R.string.na);
        }
        final String formatter = lastPrice + " (%s)";
        if (securityCompactDTO.risePercent != null)
        {
            lastPriceAndRise = THSignedPercentage.builder(securityCompactDTO.risePercent * 100)
                    .relevantDigitCount(3)
                    .signTypeArrow()
                    .withSign()
                    .withDefaultColor()
                    .format(formatter)
                    .build()
                    .createSpanned();
        }
        else
        {
            lastPriceAndRise = lastPrice;
        }
        //</editor-fold>

        //<editor-fold desc="Share Count">
        if ((positionDTO.positionStatus == PositionStatus.CLOSED
                || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED))
        {
            shareCountVisibility = View.GONE;
        }
        else
        {
            shareCountVisibility = View.VISIBLE;
        }
        if (positionDTO.shares == null)
        {
            shareCountText = resources.getString(R.string.position_share_count_qty_prefix, resources.getString(R.string.na));
        }
        else
        {
            shareCountText = THSignedNumber.builder(Math.abs(positionDTO.shares))
                    .format(resources.getString(R.string.position_share_count_qty_prefix))
                    .build()
                    .createSpanned();
        }

        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            shareCountHeader = resources.getString(R.string.position_share_count_header_fx);
            if (positionDTO.positionStatus == PositionStatus.CLOSED
                    || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED
                    || currentUserId.get() != positionDTO.userId)
            {
                btnCloseVisibility = View.GONE;
            }
            else
            {
                btnCloseVisibility = View.VISIBLE;
            }
        }
        else
        {
            shareCountHeader = resources.getString(R.string.position_share_count_header);
            btnCloseVisibility = View.GONE;
        }

        shareCountRowVisibility = (positionDTO.isClosed() != null && positionDTO.isClosed())
                ? View.GONE
                : View.VISIBLE;
        if (positionDTO.shares == null)
        {
            shareCount = resources.getString(R.string.na);
        }
        else
        {
            shareCount = THSignedNumber.builder(Math.abs(positionDTO.shares))
                    .build()
                    .createSpanned();
        }
        //</editor-fold>

        lastAmountContainerVisibility = securityCompactDTO instanceof FxSecurityCompactDTO ? View.GONE : View.VISIBLE;

        final Double realisedPLRefCcy = positionDTO.realizedPLRefCcy;
        final Double unrealisedPLRefCcy = positionDTO.unrealizedPLRefCcy;

        //<editor-fold desc="Percent and Gain">
        final Double gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
                ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                : positionDTO.getROISinceInception();
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            positionPercentVisibility = View.GONE;
            positionPercent = "";
        }
        else
        {
            positionPercentVisibility = View.VISIBLE;
            positionPercent = gainPercent == null
                    ? na
                    : THSignedPercentage.builder(gainPercent * 100.0)
                            .signTypeArrow()
                            .withSign()
                            .withDefaultColor()
                            .relevantDigitCount(3)
                            .build()
                            .createSpanned();
        }

        if (gainPercent == null || gainPercent == 0)
        {
            gainIndicator = R.drawable.default_image;
            gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
        }
        else if (gainPercent > 0)
        {
            gainIndicator = R.drawable.indicator_up;
            gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
        }
        else
        {
            gainIndicator = R.drawable.indicator_down;
            gainLossHeader = resources.getString(R.string.position_realised_loss_header) + ":";
        }

        final double gain = (realisedPLRefCcy != null ? realisedPLRefCcy : 0)
                + (unrealisedPLRefCcy != null ? unrealisedPLRefCcy : 0);
        gainLossColor = resources.getColor(gain == 0
                ? R.color.black
                : gain > 0
                        ? R.color.number_up
                        : R.color.number_down);
        gainLoss = THSignedMoney.builder(gain)
                .currency(positionDTO.getNiceCurrency())
                .withOutSign()
                .relevantDigitCount(3)
                .build()
                .createSpanned();
        gainLossPercent = gainPercent == null
                ? ""
                : THSignedPercentage.builder(gainPercent * 100)
                        .signTypePlusMinusAlways()
                        .relevantDigitCount(3)
                        .format("(%s)")
                        .build()
                        .createSpanned();
        //</editor-fold>

        totalInvested = THSignedMoney.builder(positionDTO.sumInvestedAmountRefCcy)
                .currency(positionDTO.getNiceCurrency())
                .relevantDigitCount(3)
                .build()
                .createSpanned();

        //<editor-fold desc="Unrealised">
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            unrealisedPLVisibility = View.VISIBLE;
            if (unrealisedPLRefCcy != null)
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
                unrealisedPL =
                        THSignedMoney.builder(PLR)
                                .currency(positionDTO.getNiceCurrency())
                                .signTypePlusMinusAlways()
                                .withDefaultColor()
                                .build()
                                .createSpanned();
            }
            else
            {
                unrealisedPL = resources.getString(R.string.na);
            }
        }
        else
        {
            unrealisedPLVisibility = View.GONE;
            unrealisedPL = resources.getString(R.string.na);
        }
        //</editor-fold>

        //<editor-fold desc="Last Amount">
        Boolean isOpen = positionDTO.isOpen();
        Boolean isClosed = positionDTO.isClosed();
        lastAmountHeaderVisibility = isOpen == null || isOpen
                ? View.GONE
                : View.VISIBLE;
        String lastAmountFormat = resources.getString(R.string.position_last_amount_header);
        if (isClosed != null && isClosed && realisedPLRefCcy != null)
        {
            lastAmount = THSignedMoney.builder(realisedPLRefCcy)
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
            lastAmount = THSignedMoney.builder(positionDTO.marketValueRefCcy)
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
            lastAmount = na;
        }
        //</editor-fold>
    }
}
