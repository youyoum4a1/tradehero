package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.OwnedPositionId;

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

    @Override protected PositionLongInnerViewHolder.OnPositionLongInnerClickedListener createDefaultPositionClickedListener()
    {
        return new PositionLongInnerViewHolder.OnPositionLongInnerClickedListener()
        {
            @Override public void onMoreInfoClicked(OwnedPositionId clickedOwnedPositionId)
            {
                notifyMoreInfoRequested(clickedOwnedPositionId);
            }

            @Override public void onTradeHistoryClicked(OwnedPositionId clickedOwnedPositionId)
            {
                notifyTradeHistoryRequested(clickedOwnedPositionId);
            }

            @Override public void onBuyClicked(OwnedPositionId clickedOwnedPositionId)
            {
                notifyBuyRequested(clickedOwnedPositionId);
            }

            @Override public void onSellClicked(OwnedPositionId clickedOwnedPositionId)
            {
                notifySellRequested(clickedOwnedPositionId);
            }

            @Override public void onAddAlertClicked(OwnedPositionId clickedOwnedPositionId)
            {
                notifyAddAlertRequested(clickedOwnedPositionId);
            }

            @Override public void onStockInfoClicked(OwnedPositionId clickedOwnedPositionId)
            {
                notifyStockInfoRequested(clickedOwnedPositionId);
            }
        };
    }

    //<editor-fold desc="Notify Methods">
    protected void notifyBuyRequested(OwnedPositionId clickedOwnedPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onBuyClicked(position, clickedOwnedPositionId);
        }
    }

    protected void notifySellRequested(OwnedPositionId clickedOwnedPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onSellClicked(position, clickedOwnedPositionId);
        }
    }

    protected void notifyAddAlertRequested(OwnedPositionId clickedOwnedPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onAddAlertClicked(position, clickedOwnedPositionId);
        }
    }

    protected void notifyStockInfoRequested(OwnedPositionId clickedOwnedPositionId)
    {
        OnListedPositionInnerLongClickedListener listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onStockInfoClicked(position, clickedOwnedPositionId);
        }
    }
    //</editor-fold>

    public static interface OnListedPositionInnerLongClickedListener extends PositionView.OnListedPositionInnerQuickClickedListener
    {
        void onBuyClicked(int position, OwnedPositionId clickedOwnedPositionId);
        void onSellClicked(int position, OwnedPositionId clickedOwnedPositionId);
        void onAddAlertClicked(int position, OwnedPositionId clickedOwnedPositionId);
        void onStockInfoClicked(int position, OwnedPositionId clickedOwnedPositionId);
    }
}
