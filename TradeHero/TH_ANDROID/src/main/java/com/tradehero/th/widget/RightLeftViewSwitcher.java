package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewSwitcher;
import com.tradehero.thm.R;

public class RightLeftViewSwitcher extends ViewSwitcher
{
    public boolean firstViewIsOnTheRight;

    //<editor-fold desc="Constructors">
    public RightLeftViewSwitcher(Context context)
    {
        super(context);
    }

    public RightLeftViewSwitcher(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setAttributes(attrs);
    }
    //</editor-fold>

    protected void setAttributes(AttributeSet attrs)
    {
        firstViewIsOnTheRight = attrs.getAttributeBooleanValue(R.styleable.RightLeftViewSwitcher_firstViewIsOnTheRight, false);
    }

    @Override public void setDisplayedChild(int whichChild)
    {
        prepareSlide(whichChild);
        super.setDisplayedChild(whichChild);
    }

    protected void prepareSlide(int whichChild)
    {
        if (aboutToSlideToLeft(whichChild))
        {
            prepareSlideToLeft();
        }
        else
        {
            prepareSlideToRight();
        }
    }

    protected boolean aboutToSlideToLeft(int whichChild)
    {
        switch (whichChild)
        {
            case 0:
                return !firstViewIsOnTheRight;
            case 1:
                return firstViewIsOnTheRight;
            default:
                throw new IllegalArgumentException("Unhandled whichChild " + whichChild);
        }
    }

    protected void prepareSlideToRight()
    {
        setOutAnimation(getContext(), R.anim.slide_right_out);
        setInAnimation(getContext(), R.anim.slide_left_in);
    }

    protected void prepareSlideToLeft()
    {
        setOutAnimation(getContext(), R.anim.slide_left_out);
        setInAnimation(getContext(), R.anim.slide_right_in);
    }
}
