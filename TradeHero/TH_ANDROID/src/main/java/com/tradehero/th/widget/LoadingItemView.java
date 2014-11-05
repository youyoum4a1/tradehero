package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.common.persistence.LoadingDTO;
import com.tradehero.th.api.DTOView;

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
