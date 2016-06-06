package com.androidth.general.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.androidth.general.common.persistence.LoadingDTO;
import com.androidth.general.api.DTOView;

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
