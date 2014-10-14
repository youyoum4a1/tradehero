package com.tradehero.th.fragments.market;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import org.jetbrains.annotations.NotNull;

public class ExchangeSpinner extends Spinner
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public ExchangeSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override public void setSelection(int position)
    {
        super.setSelection(position);
    }

    public void setSelection(@NotNull ExchangeCompactDTO exchangeCompactDTO)
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
}
