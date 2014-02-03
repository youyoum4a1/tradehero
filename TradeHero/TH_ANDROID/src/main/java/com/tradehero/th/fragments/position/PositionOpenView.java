package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;

/**
 * Created by julien on 30/10/13
 */
public class PositionOpenView extends AbstractPositionView<PositionDTO>
{
    private PositionPartialBottomOpenView bottomView;

    //<editor-fold desc="Constructors">
    public PositionOpenView(Context context)
    {
        super(context);
    }

    public PositionOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void initViews()
    {
        super.initViews();
        bottomView = (PositionPartialBottomOpenView) findViewById(R.id.expanding_layout);
    }

    @Override public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        this.bottomView.linkWith(positionDTO, andDisplay);
    }
}
