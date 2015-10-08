package com.tradehero.th.fragments.trade;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.ocpsoft.prettytime.PrettyTime;

public class TradeDisplayDTO
{
    public final int tradeId;
    public final Date tradeDate;
    private boolean isPrettyDate = true;
    public final CharSequence mainText;
    public final CharSequence subText;
    @NonNull public final CharSequence prettyDate;
    @NonNull public final CharSequence normalDate;

    public TradeDisplayDTO(
            @NonNull Resources resources,
            @NonNull SecurityCompactDTO securityCompactDTO,
            @NonNull PositionDTO positionDTO,
            @NonNull TradeDTO tradeDTO,
            @NonNull PrettyTime prettyTime)
    {
        this.tradeId = tradeDTO.id;
        this.tradeDate = tradeDTO.dateTime;

        //<editor-fold desc="Action text">
        boolean isBuy = tradeDTO.quantity >= 0;
        @StringRes int actionStringRes = isBuy ? R.string.bought : R.string.sold;
        @StringRes int unitStringRes = securityCompactDTO instanceof FxSecurityCompactDTO ? R.string.fx_unit : R.string.security_unit;

        THSignedNumber tradeQuantityL = THSignedNumber.builder((double) Math.abs(tradeDTO.quantity))
                .withOutSign()
                .build();
        THSignedNumber tradeValueL = THSignedMoney.builder(tradeDTO.unitPriceSecCcy)
                .withOutSign()
                .currency(securityCompactDTO.currencyDisplay)
                .build();
        mainText = resources.getString(
                R.string.trade_action_format,
                resources.getString(actionStringRes),
                resources.getString(unitStringRes),
                tradeQuantityL.toString(),
                tradeValueL.toString());
        //</editor-fold>

        //<editor-fold desc="Date">
        if (tradeDate != null)
        {
            prettyDate = prettyTime.format(tradeDTO.dateTime);
            DateFormat sdf = DateFormat.getDateTimeInstance();
            sdf.setTimeZone(TimeZone.getDefault());
            normalDate = sdf.format(tradeDTO.dateTime);
        }
        else
        {
            normalDate = "";
            prettyDate = "";
        }
        //</editor-fold>

        double numberToDisplayRefCcy = isBuy ? tradeDTO.quantity * tradeDTO.unitPriceSecCcy : tradeDTO.realizedPLAfterTradeRefCcy;
        String plFormat = resources.getString(isBuy ? R.string.position_invested : numberToDisplayRefCcy < 0
                ? R.string.position_realised_loss_header_format
                : R.string.position_realised_profit_header_format);

        THSignedMoney.Builder<?> builder = THSignedMoney.builder(numberToDisplayRefCcy)
                .withOutSign()
                .currency(securityCompactDTO.currencyDisplay)
                .with000Suffix()
                .useShortSuffix()
                .relevantDigitCount(4)
                .format(plFormat);

        if (!isBuy)
        {
            builder.withDefaultColor();
        }

        subText = builder.build()
                .createSpanned();
    }

    @NonNull
    protected CharSequence getTradeDateText()
    {
        if (isPrettyDate)
        {
            return prettyDate;
        }
        else
        {
            return normalDate;
        }
    }

    public void togglePrettyDate()
    {
        isPrettyDate = !isPrettyDate;
    }
}
