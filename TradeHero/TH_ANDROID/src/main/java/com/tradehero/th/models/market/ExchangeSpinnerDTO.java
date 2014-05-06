package com.tradehero.th.models.market;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;

public class ExchangeSpinnerDTO extends ExchangeDTO implements CharSequence
{
    private Context context;

    //<editor-fold desc="Constructors">
    public ExchangeSpinnerDTO(Context context)
    {
        this.context = context;
        // This will in effect be the "All Exchanges"
    }

    public ExchangeSpinnerDTO(Context context, ExchangeDTO exchangeDTO)
    {
        super(exchangeDTO);
        this.context = context;
    }
    //</editor-fold>

    @Override public String toString()
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
