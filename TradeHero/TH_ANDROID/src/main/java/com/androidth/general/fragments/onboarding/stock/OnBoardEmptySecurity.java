package com.androidth.general.fragments.onboarding.stock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.androidth.general.api.DTOView;

public class OnBoardEmptySecurity extends TextView
    implements DTOView<SelectableSecurityDTO>
{
    //<editor-fold desc="Constructors">
    public OnBoardEmptySecurity(Context context)
    {
        super(context);
    }

    public OnBoardEmptySecurity(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardEmptySecurity(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override public void display(SelectableSecurityDTO dto)
    {
        // Do nothing
    }
}
