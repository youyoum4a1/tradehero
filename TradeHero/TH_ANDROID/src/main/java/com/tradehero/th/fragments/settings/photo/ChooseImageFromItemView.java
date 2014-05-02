package com.tradehero.th.fragments.settings.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.api.DTOView;

public class ChooseImageFromItemView extends LinearLayout
    implements DTOView<ChooseImageFromDTO>
{
    private ChooseImageFromItemViewHolder viewHolder;

    //<editor-fold desc="Constructors">
    public ChooseImageFromItemView(Context context)
    {
        super(context);
    }

    public ChooseImageFromItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ChooseImageFromItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        viewHolder = new ChooseImageFromItemViewHolder(getResources(), this);
    }

    @Override public void display(ChooseImageFromDTO dto)
    {
        viewHolder.display(dto);
    }
}
