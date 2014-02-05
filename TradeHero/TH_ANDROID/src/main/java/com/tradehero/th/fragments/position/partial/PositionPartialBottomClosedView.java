package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;

/**
 * Created by xavier on 2/3/14.
 */
public class PositionPartialBottomClosedView extends AbstractPositionPartialBottomClosedView<PositionDTO, ExpandableListItem<PositionDTO>>
{
    public static final String TAG = PositionPartialBottomClosedView.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public PositionPartialBottomClosedView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
