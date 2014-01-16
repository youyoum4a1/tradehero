package com.tradehero.th.models.market;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;

/**
 * Created by xavier on 1/16/14.
 */
public class ExchangeSpinnerDTO extends ExchangeDTO implements CharSequence
{
    public static final String TAG = ExchangeSpinnerDTO.class.getSimpleName();

    private Context context;

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
