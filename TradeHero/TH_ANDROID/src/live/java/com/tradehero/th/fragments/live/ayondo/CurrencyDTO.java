package com.tradehero.th.fragments.live.ayondo;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.api.kyc.Currency;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDTO
{
    @NonNull public final Currency currency;
    @NonNull public final String text;

    public CurrencyDTO(@NonNull Resources resources, @NonNull Currency currency)
    {
        this(currency, resources.getString(currency.dropDownText));
    }

    public CurrencyDTO(@NonNull Currency currency, @NonNull String text)
    {
        this.currency = currency;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull static List<CurrencyDTO> createList(@NonNull Resources resources, @NonNull List<Currency> currencies)
    {
        List<CurrencyDTO> created = new ArrayList<>();
        for (Currency currency : currencies)
        {
            created.add(new CurrencyDTO(resources, currency));
        }
        return created;
    }
}
