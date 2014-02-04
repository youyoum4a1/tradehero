package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;

/**
 * Created by xavier on 2/3/14.
 */
public class PositionClosedView extends AbstractPositionClosedView<PositionDTO>
{
    public static final String TAG = PositionClosedView.class.getSimpleName();

    private PositionPartialBottomClosedView bottomView;

    //<editor-fold desc="Constructors">
    public PositionClosedView(Context context)
    {
        super(context);
    }

    public PositionClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void initViews()
    {
        super.initViews();
        bottomView = (PositionPartialBottomClosedView) findViewById(R.id.expanding_layout);
    }

    @Override public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        this.bottomView.linkWith(positionDTO, andDisplay);
    }
}
