package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.widget.position.partial.PositionPartialBottomOpenView;

/**
 * Created by julien on 30/10/13
 */
public class PositionOpenView extends AbstractPositionView
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

    @Override public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        super.linkWith(ownedPositionId, andDisplay);
        this.bottomView.linkWith(ownedPositionId, andDisplay);
    }
}
