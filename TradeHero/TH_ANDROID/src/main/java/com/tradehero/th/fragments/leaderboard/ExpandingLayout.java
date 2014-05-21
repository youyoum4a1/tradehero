package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ExpandingLayout extends LinearLayout
{
    private OnExpandListener mOnExpandListener;

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

    @Override protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);

    }

    private void notifyExpand(boolean expand)
    {
        if (mOnExpandListener != null)
        {
            mOnExpandListener.onExpand(expand);
        }
    }

    public void expand(boolean expand)
    {
        setVisibility(expand ? View.VISIBLE : View.GONE);
        notifyExpand(expand);
    }


    public void addOnExpandListener(OnExpandListener onExpandListener)
    {
        this.mOnExpandListener = onExpandListener;
    }

    public static interface OnExpandListener {
        void onExpand(boolean expand);
    }

}
