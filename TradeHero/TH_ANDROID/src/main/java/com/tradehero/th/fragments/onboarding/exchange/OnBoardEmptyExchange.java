package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.api.DTOView;

public class OnBoardEmptyExchange extends TextView
    implements DTOView<OnBoardExchangeDTO>
{
    //<editor-fold desc="Constructors">
    public OnBoardEmptyExchange(Context context)
    {
        super(context);
    }

    public OnBoardEmptyExchange(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardEmptyExchange(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override public void display(OnBoardExchangeDTO dto)
    {
        // Do nothing
    }
}
