package com.tradehero.th.widget.position;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.FiledPositionId;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 10/21/13 Time: 11:11 AM To change this template use File | Settings | File Templates. */
abstract public class PositionView<
            ParentOnClickedListenerType extends PositionView.OnListedPositionInnerQuickClickedListener,
            OnClickedListenerType extends PositionQuickInnerViewHolder.OnPositionQuickInnerClickedListener,
            HolderViewType extends PositionQuickInnerViewHolder<OnClickedListenerType>
        >
        extends RelativeLayout
        implements DTOView<FiledPositionId>
{
    protected HolderViewType viewHolder;
    protected int position;
    protected WeakReference<ParentOnClickedListenerType> parentPositionClickedListener = new WeakReference<>(null);
    private boolean clicked = false;
    protected FiledPositionId filedPositionId;

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
        //moreInfoRequestedListener = new PositionQuickInnerViewHolder.OnMoreInfoRequestedListener()
        //{
        //    @Override public void onMoreInfoRequested()
        //    {
        //        notifyPositionClicked();
        //    }
        //};
        //viewHolder.setMoreInfoRequestedListener(moreInfoRequestedListener);
        onPositionClickedListener = createDefaultPositionClickedListener();
        viewHolder.setPositionClickedListener(onPositionClickedListener);
        viewHolder.initViews(getRootView());
    }

    abstract protected OnClickedListenerType createDefaultPositionClickedListener();

    @Override public void display(FiledPositionId dto)
    {
        viewHolder.linkWith(dto, true);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        clicked = false;
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_MOVE)
        {
            return false;
        }

        boolean intercepted = super.onInterceptTouchEvent(ev);

        if (!intercepted && action == MotionEvent.ACTION_DOWN)
        {
            clicked = true;
            intercepted = false; // If it is set to true, the buttons don't get notified
        }
        return intercepted;
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        if (clicked)
        {
            notifyMoreInfoRequested();
            clicked = false;
        }
        return super.onTouchEvent(event);
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

    protected void notifyMoreInfoRequested()
    {
        ParentOnClickedListenerType listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onMoreInfoClicked(position, filedPositionId);
        }
    }

    protected void notifyTradeHistoryRequested()
    {
        ParentOnClickedListenerType listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onTradeHistoryClicked(position, filedPositionId);
        }
    }

    public static interface OnListedPositionInnerQuickClickedListener
    {
        void onMoreInfoClicked(int position, FiledPositionId clickedFiledPositionId);
        void onTradeHistoryClicked(int position, FiledPositionId clickedFiledPositionId);
    }
}
