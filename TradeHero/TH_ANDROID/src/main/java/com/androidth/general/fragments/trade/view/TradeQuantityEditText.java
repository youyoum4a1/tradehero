package com.androidth.general.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class TradeQuantityEditText extends EditText
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public TradeQuantityEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

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
