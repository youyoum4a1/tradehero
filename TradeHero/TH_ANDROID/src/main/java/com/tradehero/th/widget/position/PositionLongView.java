package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.FiledPositionId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 6:49 PM To change this template use File | Settings | File Templates. */
public class PositionLongView extends RelativeLayout
        implements DTOView<FiledPositionId>
{
    private PositionLongViewHolder positionLongViewHolder;

    //<editor-fold desc="Constructors">
    public PositionLongView(Context context)
    {
        super(context);
    }

    public PositionLongView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionLongView(Context context, AttributeSet attrs, int defStyle)
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
        positionLongViewHolder = new PositionLongViewHolder();
        positionLongViewHolder.initViews(getRootView());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (positionLongViewHolder != null)
        {
            positionLongViewHolder.displayTradeHistoryButton();
        }
    }

    @Override public void display(FiledPositionId dto)
    {
        positionLongViewHolder.linkWith(dto, true);
    }
}
