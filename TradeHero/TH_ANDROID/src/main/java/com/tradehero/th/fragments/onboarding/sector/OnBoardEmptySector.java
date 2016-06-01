package com.ayondo.academy.fragments.onboarding.sector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.ayondo.academy.api.DTOView;

public class OnBoardEmptySector extends TextView
    implements DTOView<SelectableSectorDTO>
{
    //<editor-fold desc="Constructors">
    public OnBoardEmptySector(Context context)
    {
        super(context);
    }

    public OnBoardEmptySector(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardEmptySector(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override public void display(SelectableSectorDTO dto)
    {
        // Do nothing
    }
}
