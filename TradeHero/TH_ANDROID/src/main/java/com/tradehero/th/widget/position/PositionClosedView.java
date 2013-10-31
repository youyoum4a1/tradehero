package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.widget.position.partial.PositionPartialBottomClosedView;

/**
 * Created by julien on 30/10/13
 */
public class PositionClosedView extends AbstractPositionView
{
    private PositionPartialBottomClosedView bottomView;

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

    @Override protected void initViews()
    {
        super.initViews();
        bottomView = (PositionPartialBottomClosedView) findViewById(R.id.expanding_layout);

    }

    @Override public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        super.linkWith(ownedPositionId, andDisplay);
        this.bottomView.linkWith(ownedPositionId, andDisplay);
    }
}
