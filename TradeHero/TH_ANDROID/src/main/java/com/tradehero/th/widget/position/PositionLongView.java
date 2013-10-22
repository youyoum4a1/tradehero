package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.FiledPositionId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 6:49 PM To change this template use File | Settings | File Templates. */
public class PositionLongView extends PositionView<
        PositionLongView.OnListedPositionInnerLongClickedListener,
        PositionLongViewHolder.OnPositionLongInnerClickedListener,
        PositionLongViewHolder>
{
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

    @Override protected void init ()
    {
        viewHolder = new PositionLongViewHolder();
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

    @Override protected PositionLongInnerViewHolder.OnPositionLongInnerClickedListener createDefaultPositionClickedListener()
    {
        return new PositionLongInnerViewHolder.OnPositionLongInnerClickedListener()
        {
            @Override public void onMoreInfoClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyMoreInfoRequested(clickedFiledPositionId);
            }

            @Override public void onTradeHistoryClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyTradeHistoryRequested(clickedFiledPositionId);
            }

            @Override public void onBuyClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyBuyRequested(clickedFiledPositionId);
            }

            @Override public void onSellClicked(FiledPositionId clickedFiledPositionId)
            {
                notifySellRequested(clickedFiledPositionId);
            }

            @Override public void onAddAlertClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyAddAlertRequested(clickedFiledPositionId);
            }

            @Override public void onStockInfoClicked(FiledPositionId clickedFiledPositionId)
            {
                notifyStockInfoRequested(clickedFiledPositionId);
            }
        };
    }

    //<editor-fold desc="Notify Methods">
    protected void notifyBuyRequested(FiledPositionId clickedFiledPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onBuyClicked(position, clickedFiledPositionId);
        }
    }

    protected void notifySellRequested(FiledPositionId clickedFiledPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onSellClicked(position, clickedFiledPositionId);
        }
    }

    protected void notifyAddAlertRequested(FiledPositionId clickedFiledPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onAddAlertClicked(position, clickedFiledPositionId);
        }
    }

    protected void notifyStockInfoRequested(FiledPositionId clickedFiledPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onStockInfoClicked(position, clickedFiledPositionId);
        }
    }
    //</editor-fold>

    public static interface OnListedPositionInnerLongClickedListener extends PositionView.OnListedPositionInnerQuickClickedListener
    {
        void onBuyClicked(int position, FiledPositionId clickedFiledPositionId);
        void onSellClicked(int position, FiledPositionId clickedFiledPositionId);
        void onAddAlertClicked(int position, FiledPositionId clickedFiledPositionId);
        void onStockInfoClicked(int position, FiledPositionId clickedFiledPositionId);
    }
}
