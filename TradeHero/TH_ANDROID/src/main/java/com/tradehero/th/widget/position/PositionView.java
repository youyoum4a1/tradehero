package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.OwnedPositionId;

import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 10/21/13 Time: 11:11 AM To change this template use File | Settings | File Templates. */
abstract public class PositionView<
            ParentOnClickedListenerType extends PositionView.OnListedPositionInnerQuickClickedListener,
            OnClickedListenerType extends PositionQuickInnerViewHolder.OnPositionQuickInnerClickedListener,
            HolderViewType extends PositionQuickInnerViewHolder<OnClickedListenerType>
        >
        extends RelativeLayout
        implements DTOView<OwnedPositionId>
{
    public static final String TAG = PositionView.class.getSimpleName();

    protected HolderViewType viewHolder;
    protected int position;
    protected WeakReference<ParentOnClickedListenerType> parentPositionClickedListener = new WeakReference<>(null);
    protected OwnedPositionId ownedPositionId;

    protected OnClickedListenerType onPositionClickedListener;

    //<editor-fold desc="Constructors">
    public PositionView(Context context)
    {
        super(context);
    }

    public PositionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionView(Context context, AttributeSet attrs, int defStyle)
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
        onPositionClickedListener = createDefaultPositionClickedListener();
        viewHolder.setPositionClickedListener(onPositionClickedListener);
        viewHolder.initViews(getRootView());
        getRootView().setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                notifyMoreInfoRequested(ownedPositionId);
            }
        });
    }

    abstract protected OnClickedListenerType createDefaultPositionClickedListener();

    @Override public void display(OwnedPositionId dto)
    {
        this.ownedPositionId = dto;
        viewHolder.linkWith(dto, true);
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public void setPositionClickedListener(ParentOnClickedListenerType parentPositionClickedListener)
    {
        this.parentPositionClickedListener = new WeakReference<>(parentPositionClickedListener);
    }

    protected void notifyMoreInfoRequested(OwnedPositionId clickedOwnedPositionId)
    {
        ParentOnClickedListenerType listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onMoreInfoClicked(position, clickedOwnedPositionId);
        }
    }

    protected void notifyTradeHistoryRequested(OwnedPositionId clickedOwnedPositionId)
    {
        ParentOnClickedListenerType listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onTradeHistoryClicked(position, clickedOwnedPositionId);
        }
    }

    public static interface OnListedPositionInnerQuickClickedListener
    {
        void onMoreInfoClicked(int position, OwnedPositionId clickedOwnedPositionId);
        void onTradeHistoryClicked(int position, OwnedPositionId clickedOwnedPositionId);
    }
}
