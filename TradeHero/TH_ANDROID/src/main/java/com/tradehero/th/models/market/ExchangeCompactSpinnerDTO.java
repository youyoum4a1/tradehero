package com.tradehero.th.models.market;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import org.jetbrains.annotations.NotNull;

public class ExchangeCompactSpinnerDTO extends ExchangeCompactDTO implements CharSequence
{
    private final Context context;

    //<editor-fold desc="Constructors">
    public ExchangeCompactSpinnerDTO(@NotNull Context context)
    {
        this.context = context;
        // This will in effect be the "All Exchanges"
    }

    public ExchangeCompactSpinnerDTO(@NotNull Context context, @NotNull ExchangeCompactDTO exchangeDTO)
    {
        super(exchangeDTO);
        this.context = context;
    }
    //</editor-fold>

    @Override @NotNull public String toString()
    {
        if (name == null && desc == null)
        {
            return context.getString(R.string.trending_filter_exchange_all);
        }
        return context.getString(R.string.trending_filter_exchange_drop_down, name, desc);
    }

    //<editor-fold desc="CharSequence">
    @Override public CharSequence subSequence(int start, int end)
    {
        return toString().subSequence(start, end);
    }

    @Override public char charAt(int index)
    {
        return toString().charAt(index);
    }

    @Override public int length()
    {
        return toString().length();
    }
    //</editor-fold>
}
