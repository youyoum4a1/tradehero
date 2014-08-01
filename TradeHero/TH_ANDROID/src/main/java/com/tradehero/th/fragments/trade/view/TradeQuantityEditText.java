package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class TradeQuantityEditText extends EditText
{

    public TradeQuantityEditText(Context context)
    {
        super(context);
    }

    public TradeQuantityEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TradeQuantityEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void onSelectionChanged(int start, int end)
    {
        CharSequence text = getText();
        if (text != null)
        {
            if (start != text.length() || end != text.length())
            {
                setSelection(text.length(), text.length());
                return;
            }
        }
        super.onSelectionChanged(start, end);
    }
}
