package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ExpandingLayout extends LinearLayout
{
    private OnExpandListener expandListener;

    //<editor-fold desc="Constructors">
    public ExpandingLayout(Context context)
    {
        super(context);
    }

    public ExpandingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ExpandingLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);

    }

    private void notifyExpand(boolean expand)
    {
        OnExpandListener expandListenerCopy = expandListener;
        if (expandListenerCopy != null)
        {
            expandListenerCopy.onExpand(expand);
        }
    }

    public void expand(boolean expand)
    {
        setVisibility(expand ? View.VISIBLE : View.GONE);
        notifyExpand(expand);
    }

    public void setOnExpandListener(OnExpandListener expandListener)
    {
        this.expandListener = expandListener;
    }

    public static interface OnExpandListener
    {
        void onExpand(boolean expand);
    }
}
