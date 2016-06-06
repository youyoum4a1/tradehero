package com.androidth.general.fragments.onboarding.hero;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.androidth.general.api.DTOView;

public class OnBoardEmptyUsers extends TextView
    implements DTOView<SelectableUserDTO>
{
    //<editor-fold desc="Constructors">
    public OnBoardEmptyUsers(Context context)
    {
        super(context);
    }

    public OnBoardEmptyUsers(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardEmptyUsers(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override public void display(SelectableUserDTO dto)
    {
        // Do nothing
    }
}
