package com.tradehero.th.models.trade;

import android.content.res.Resources;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.THSignedNumber;
import javax.inject.Inject;

public class TradeDTOUtils
{
    @Inject public TradeDTOUtils()
    {
        super();
    }

    public void setRealizedPLLook(TextView textView, TradeDTO tradeDTO, String refCurrency)
    {
        textView.setText(getRealizedPL(textView.getResources(), tradeDTO, refCurrency));
        textView.setTextColor(textView.getResources().getColor(ColorUtils.getColorResourceForNumber(tradeDTO.realized_pl_after_trade)));
    }

    private String getRealizedPL(Resources resources, TradeDTO tradeDTO, String refCurrency)
    {
        if (tradeDTO != null)
        {
            THSignedNumber formattedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    tradeDTO.realized_pl_after_trade,
                    THSignedNumber.WITHOUT_SIGN,
                    refCurrency);
            return formattedNumber.toString();
        }
        else
        {
            return resources.getString(R.string.na);
        }
    }
}
