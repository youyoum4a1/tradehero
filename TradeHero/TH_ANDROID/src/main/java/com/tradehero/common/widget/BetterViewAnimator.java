package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewAnimator;

public class BetterViewAnimator extends ViewAnimator
{
    //region Constructors
    public BetterViewAnimator(Context context)
    {
        super(context);
    }

    public BetterViewAnimator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //endregion

    public void setDisplayedChildByLayoutId(int childLayoutId)
    {
        if (getDisplayedChildLayoutId() == childLayoutId)
        {
            return;
        }

        for (int i = 0; i < getChildCount(); ++i)
        {
            View child = getChildAt(i);
            if (child != null && childLayoutId == child.getId())
            {
                super.setDisplayedChild(i);
                return;
            }
        }
    }

    public int getDisplayedChildLayoutId()
    {
        View displayedChild = getChildAt(getDisplayedChild());
        if (displayedChild != null)
        {
            return displayedChild.getId();
        }
        else
        {
            return 0;
        }
    }


}
