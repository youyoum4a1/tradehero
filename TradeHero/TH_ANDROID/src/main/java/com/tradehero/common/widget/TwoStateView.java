package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/14/14 Time: 11:54 AM Copyright (c) TradeHero
 */
public class TwoStateView extends RelativeLayout
    implements Checkable
{
    private boolean checked;
    private WeakReference<OnStateChange> stateChangeListener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public TwoStateView(Context context)
    {
        super(context);
    }

    public TwoStateView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TwoStateView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        super.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                toggle();
            }
        });
    }

    //<editor-fold desc="Checkable">
    @Override public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    @Override public boolean isChecked()
    {
        return checked;
    }

    @Override public void toggle()
    {
        setChecked(!checked);

        if (stateChangeListener != null)
        {
            OnStateChange listener = stateChangeListener.get();
            if (listener != null)
            {
                listener.onStateChanged(this, isChecked());
            }
        }
    }
    //</editor-fold>

    @Override public final void setOnClickListener(OnClickListener l)
    {
        throw new IllegalAccessError("This method should only be call internally");
    }

    public void setOnStateChange(OnStateChange onStateChange)
    {
        this.stateChangeListener = new WeakReference<>(onStateChange);
    }

    public static interface OnStateChange
    {
        public void onStateChanged(View view, boolean state);
    }

    public boolean isFirstState()
    {
        return !isChecked();
    }

    public boolean isSecondState()
    {
        return isChecked();
    }
}
