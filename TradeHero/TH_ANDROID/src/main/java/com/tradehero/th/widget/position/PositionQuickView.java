package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.FiledPositionId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 6:49 PM To change this template use File | Settings | File Templates. */
public class PositionQuickView extends PositionView<
        PositionView.OnListedPositionInnerQuickClickedListener,
        PositionQuickInnerViewHolder.OnPositionQuickInnerClickedListener,
        PositionQuickViewHolder>
{
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

    @Override protected void init ()
    {
        viewHolder = new PositionQuickViewHolder();
        super.init();

    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (viewHolder != null)
        {
            viewHolder.displayTradeHistoryButton();
        }
    }

    public void onDestroy()
    {
        if (viewHolder != null)
        {
        }
    }

    @Override protected PositionQuickInnerViewHolder.OnPositionQuickInnerClickedListener createDefaultPositionClickedListener()
    {
        return new PositionQuickInnerViewHolder.OnPositionQuickInnerClickedListener()
        {
            @Override public void onMoreInfoClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyMoreInfoRequested(clickedFiledPositionId);
            }

            @Override public void onTradeHistoryClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyTradeHistoryRequested(clickedFiledPositionId);
            }
        };
    }
}
