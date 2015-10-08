package com.tradehero.th.fragments.trade;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import org.oshkimaadziig.george.androidutils.SpanFormatter;

public class TradeSummaryDisplayDTO
{
    public final CharSequence plValueHeader;
    public final CharSequence plValue;
    public final CharSequence totalInvested;
    public final CharSequence averagePrice;

    public TradeSummaryDisplayDTO(Resources resources, SecurityCompactDTO securityCompactDTO, PositionDTO positionDTO)
    {
        String na = resources.getString(R.string.na);
        boolean isClosed = (positionDTO.isClosed() != null && positionDTO.isClosed());
        Double pLRefCcy = (isClosed) ? positionDTO.realizedPLRefCcy : positionDTO.unrealizedPLRefCcy;

        plValueHeader = resources.getString(pLRefCcy != null && pLRefCcy < 0
                ? isClosed ? R.string.position_realised_loss_header : R.string.position_unrealised_loss_header
                : isClosed ? R.string.position_realised_profit_header : R.string.position_unrealised_profit_header);

        CharSequence unrealisedPLValue = pLRefCcy == null
                ? na
                : THSignedMoney.builder(pLRefCcy)
                        .withOutSign()
                        .currency(positionDTO.getNiceCurrency())
                        .withDefaultColor()
                        .build()
                        .createSpanned();
        Double gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
                ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                : positionDTO.getROISinceInception();
        CharSequence unrealisedPLPercent = gainPercent == null
                ? na
                : THSignedPercentage.builder(gainPercent * 100)
                        .signTypePlusMinusAlways()
                        .relevantDigitCount(3)
                        .withDefaultColor()
                        .build()
                        .createSpanned();
        plValue = SpanFormatter.format("%1$s (%2$s)", unrealisedPLValue, unrealisedPLPercent);

        Double sumInvestedRefCcy = positionDTO.sumInvestedAmountRefCcy;
        totalInvested = sumInvestedRefCcy == null
                ? na
                : THSignedMoney.builder(sumInvestedRefCcy)
                        .withOutSign()
                        .currency(positionDTO.getNiceCurrency())
                        .build()
                        .createSpanned();

        //<editor-fold desc="Average Price Value">
        Double averagePriceRefCcy = positionDTO.averagePriceRefCcy;
        averagePrice = averagePriceRefCcy == null
                ? na
                : THSignedMoney.builder(averagePriceRefCcy)
                        .withOutSign()
                        .currency(positionDTO.getNiceCurrency())
                        .build()
                        .toString();
        //</editor-fold>

    }
}
