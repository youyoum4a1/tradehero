package com.androidth.general.fragments.live.ayondo;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.api.kyc.TradingPerQuarter;
import java.util.ArrayList;
import java.util.List;

class TradingPerQuarterDTO
{
    @NonNull public final TradingPerQuarter tradingPerQuarter;
    @NonNull public final String text;

    public TradingPerQuarterDTO(@NonNull Resources resources, @NonNull TradingPerQuarter tradingPerQuarter)
    {
        this(tradingPerQuarter, resources.getString(tradingPerQuarter.dropDownText));
    }

    public TradingPerQuarterDTO(@NonNull TradingPerQuarter tradingPerQuarter, @NonNull String text)
    {
        this.tradingPerQuarter = tradingPerQuarter;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull static List<TradingPerQuarterDTO> createList(@NonNull Resources resources, @NonNull List<TradingPerQuarter> tradingPerQuarters)
    {
        List<TradingPerQuarterDTO> created = new ArrayList<>();
        for (TradingPerQuarter tradingPerQuarter : tradingPerQuarters)
        {
            created.add(new TradingPerQuarterDTO(resources, tradingPerQuarter));
        }
        return created;
    }
}
