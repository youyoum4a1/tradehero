package com.tradehero.th.fragments.market;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.tradehero.th.api.market.ExchangeCompactDTO;

public class ExchangeSpinner extends Spinner
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public ExchangeSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    public void setSelection(@NonNull ExchangeCompactDTO exchangeCompactDTO)
    {
        SpinnerAdapter adapter = getAdapter();
        int index = 0;
        while (index < adapter.getCount())
        {
            ExchangeCompactDTO item = (ExchangeCompactDTO) adapter.getItem(index);
            if (item.name.equals(exchangeCompactDTO.name))
            {
                setSelection(index);
            }
            index++;
        }
    }

    public void setSelectionById(int id)
    {
        SpinnerAdapter adapter = getAdapter();
        int index = 0;
        while (index < adapter.getCount())
        {
            ExchangeCompactDTO item = (ExchangeCompactDTO) adapter.getItem(index);
            if (item.id == id)
            {
                setSelection(index);
            }
            index++;
        }
    }
}
