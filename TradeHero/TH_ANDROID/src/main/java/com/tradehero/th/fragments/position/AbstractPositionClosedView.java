package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;

/**
 * Created by julien on 30/10/13
 */
abstract public class AbstractPositionClosedView<PositionDTOType extends PositionDTO> extends AbstractPositionView<PositionDTOType>
{
    public static final String TAG = AbstractPositionClosedView.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public AbstractPositionClosedView(Context context)
    {
        super(context);
    }

    public AbstractPositionClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractPositionClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
