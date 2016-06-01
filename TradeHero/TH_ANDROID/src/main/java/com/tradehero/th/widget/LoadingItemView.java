package com.ayondo.academy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.common.persistence.LoadingDTO;
import com.ayondo.academy.api.DTOView;

public class LoadingItemView extends LinearLayout implements DTOView<LoadingDTO>
{
    public LoadingItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override public void display(LoadingDTO dto)
    {
        //Do nothing
    }
}
