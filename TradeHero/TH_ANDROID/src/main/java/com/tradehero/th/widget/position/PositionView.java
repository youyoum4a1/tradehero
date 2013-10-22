package com.tradehero.th.widget.position;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.tradehero.common.utils.THLog;
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
    public static final String TAG = PositionView.class.getSimpleName();

    protected HolderViewType viewHolder;
    protected int position;
    protected WeakReference<ParentOnClickedListenerType> parentPositionClickedListener = new WeakReference<>(null);
    private boolean downed = false;
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
        this.filedPositionId = dto;
        viewHolder.linkWith(dto, true);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        //clicked = false;
        final int action = MotionEventCompat.getActionMasked(ev);

        THLog.d(TAG, "onInterceptTouchEvent, MotionEvent " + action);

        //if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_MOVE)
        //{
        //    return false;
        //}

        boolean intercepted = super.onInterceptTouchEvent(ev);

        THLog.d(TAG, "onInterceptTouchEvent, intercepted: " + intercepted);

        //if (!intercepted && action == MotionEvent.ACTION_DOWN)
        //{
        //    clicked = true;
        //    intercepted = false; // If it is set to true, the buttons don't get notified
        //}
        return intercepted;
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        final int action = MotionEventCompat.getActionMasked(event);
        THLog.d(TAG, "onTouchEvent, MotionEvent " + action);
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            notifyMoreInfoRequested(filedPositionId);
            clicked = false;
        }
        boolean intercepted = super.onTouchEvent(event);
        THLog.d(TAG, "onTouchEvent, intercepted: " + intercepted);
        return intercepted;
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

    protected void notifyMoreInfoRequested(FiledPositionId clickedFiledPositionId)
    {
        ParentOnClickedListenerType listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onMoreInfoClicked(position, clickedFiledPositionId);
        }
    }

    protected void notifyTradeHistoryRequested(FiledPositionId clickedFiledPositionId)
    {
        ParentOnClickedListenerType listener = parentPositionClickedListener.get();
        if (listener != null)
        {
            listener.onTradeHistoryClicked(position, clickedFiledPositionId);
        }
    }

    public static interface OnListedPositionInnerQuickClickedListener
    {
        void onMoreInfoClicked(int position, FiledPositionId clickedFiledPositionId);
        void onTradeHistoryClicked(int position, FiledPositionId clickedFiledPositionId);
    }
}
