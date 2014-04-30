package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;

public class PositionPartialBottomOpenView
        extends AbstractPositionPartialBottomOpenView<
            PositionDTO,
            ExpandableListItem<PositionDTO>>
{
    //<editor-fold desc="Constructors">
    public PositionPartialBottomOpenView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
