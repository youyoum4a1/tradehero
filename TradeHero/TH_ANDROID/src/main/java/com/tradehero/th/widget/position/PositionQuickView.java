package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.FiledPositionId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 6:49 PM To change this template use File | Settings | File Templates. */
public class PositionQuickView extends RelativeLayout implements DTOView<FiledPositionId>
{
    private PositionQuickViewHolder positionQuickViewHolder;

    //<editor-fold desc="Constructors">
    public PositionQuickView(Context context)
    {
        super(context);
    }

    public PositionQuickView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionQuickView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    protected void init ()
    {
        positionQuickViewHolder = new PositionQuickViewHolder();
        positionQuickViewHolder.initViews(getRootView());
    }


    @Override public void display(FiledPositionId dto)
    {
        positionQuickViewHolder.linkWith(dto, true);
    }
}
