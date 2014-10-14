package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class InstantAutoCompleteTextView extends AutoCompleteTextView
{
    //<editor-fold desc="Constructors">
    public InstantAutoCompleteTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override
    public boolean enoughToFilter()
    {
        return true;
    }

    public void showAllSuggestions()
    {
        performFiltering(getText(), 0);
    }
}