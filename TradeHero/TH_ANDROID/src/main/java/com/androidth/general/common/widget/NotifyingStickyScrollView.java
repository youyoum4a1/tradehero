package com.androidth.general.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ScrollView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.androidth.general.common.utils.SDKUtils;
import java.util.ArrayList;

/**
 * Taken from https://github.com/emilsjolander/StickyScrollViewItems Feel sorry for the author but he does not make it a maven module, I have no
 * other choice :3
 *
 * Combined with https://github.com/lawloretienne/QuickReturn to allow scroll listener
 *
 * @author Emil Sjlander - sjolander.emil@gmail.com
 * @author Etienne Lawlor - lawloretienne@gmail.com
 */
public class NotifyingStickyScrollView extends ScrollView
{

    /**
     * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
     */
    public static final String STICKY_TAG = "sticky";

    /**
     * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
     */
    public static final String FLAG_NONCONSTANT = "-nonconstant";

    /**
     * Flag for views that have aren't fully opaque
     */
    public static final String FLAG_HASTRANSPARANCY = "-hastransparancy";

    private ArrayList<View> stickyViews;
    private View currentlyStickingView;
    private float stickyViewTopOffset;
    private int stickyViewLeftOffset;
    private boolean redirectTouchesToStickyView;
    private boolean clippingToPadding;
    private boolean clipToPaddingHasBeenSet;

    // region Member Variables
    private boolean mIsOverScrollEnabled = true;
    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener;
    // endregion

    private final Runnable invalidateRunnable = new Runnable()
    {

        @Override
        public void run()
        {
            if (currentlyStickingView != null)
            {
                int l = getLeftForViewRelativeOnlyChild(currentlyStickingView);
                int t = getBottomForViewRelativeOnlyChild(currentlyStickingView);
                int r = getRightForViewRelativeOnlyChild(currentlyStickingView);
                int b = (int) (getScrollY() + (currentlyStickingView.getHeight() + stickyViewTopOffset));
                invalidate(l, t, r, b);
            }
            postDelayed(this, 16);
        }
    };

    public NotifyingStickyScrollView(Context context)
    {
        this(context, null);
    }

    public NotifyingStickyScrollView(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public NotifyingStickyScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setup();
    }

    public void setup()
    {
        stickyViews = new ArrayList<>();
    }

    private int getLeftForViewRelativeOnlyChild(View v)
    {
        int left = v.getLeft();
        while (v.getParent() != getChildAt(0))
        {
            v = (View) v.getParent();
            left += v.getLeft();
        }
        return left;
    }

    private int getTopForViewRelativeOnlyChild(View v)
    {
        int top = v.getTop();
        while (v.getParent() != getChildAt(0))
        {
            v = (View) v.getParent();
            top += v.getTop();
        }
        return top;
    }

    private int getRightForViewRelativeOnlyChild(View v)
    {
        int right = v.getRight();
        while (v.getParent() != getChildAt(0))
        {
            v = (View) v.getParent();
            right += v.getRight();
        }
        return right;
    }

    private int getBottomForViewRelativeOnlyChild(View v)
    {
        int bottom = v.getBottom();
        while (v.getParent() != getChildAt(0))
        {
            v = (View) v.getParent();
            bottom += v.getBottom();
        }
        return bottom;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (!clipToPaddingHasBeenSet)
        {
            clippingToPadding = true;
        }
        notifyHierarchyChanged();
    }

    @Override
    public void setClipToPadding(boolean clipToPadding)
    {
        super.setClipToPadding(clipToPadding);
        clippingToPadding = clipToPadding;
        clipToPaddingHasBeenSet = true;
    }

    @Override
    public void addView(@NonNull View child)
    {
        super.addView(child);
        findStickyViews(child);
    }

    @Override
    public void addView(@NonNull View child, int index)
    {
        super.addView(child, index);
        findStickyViews(child);
    }

    @Override
    public void addView(@NonNull View child, int index, android.view.ViewGroup.LayoutParams params)
    {
        super.addView(child, index, params);
        findStickyViews(child);
    }

    @Override
    public void addView(@NonNull View child, int width, int height)
    {
        super.addView(child, width, height);
        findStickyViews(child);
    }

    @Override
    public void addView(@NonNull View child, android.view.ViewGroup.LayoutParams params)
    {
        super.addView(child, params);
        findStickyViews(child);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas)
    {
        super.dispatchDraw(canvas);
        if (currentlyStickingView != null)
        {
            canvas.save();
            canvas.translate(getPaddingLeft() + stickyViewLeftOffset,
                    getScrollY() + stickyViewTopOffset + (clippingToPadding ? getPaddingTop() : 0));
            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(), currentlyStickingView.getHeight());
            if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY))
            {
                showView(currentlyStickingView);
                currentlyStickingView.draw(canvas);
                hideView(currentlyStickingView);
            }
            else
            {
                currentlyStickingView.draw(canvas);
            }
            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            redirectTouchesToStickyView = true;
        }

        if (redirectTouchesToStickyView)
        {
            redirectTouchesToStickyView = currentlyStickingView != null;
            if (redirectTouchesToStickyView)
            {
                redirectTouchesToStickyView =
                        ev.getY() <= (currentlyStickingView.getHeight() + stickyViewTopOffset) &&
                                ev.getX() >= getLeftForViewRelativeOnlyChild(currentlyStickingView) &&
                                ev.getX() <= getRightForViewRelativeOnlyChild(currentlyStickingView);
            }
        }
        else if (currentlyStickingView == null)
        {
            redirectTouchesToStickyView = false;
        }
        if (redirectTouchesToStickyView)
        {
            ev.offsetLocation(0, -1 * ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean hasNotDoneActionDown = true;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev)
    {
        if (redirectTouchesToStickyView)
        {
            ev.offsetLocation(0, ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            hasNotDoneActionDown = false;
        }

        if (hasNotDoneActionDown)
        {
            MotionEvent down = MotionEvent.obtain(ev);
            down.setAction(MotionEvent.ACTION_DOWN);
            super.onTouchEvent(down);
            hasNotDoneActionDown = false;
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL)
        {
            hasNotDoneActionDown = true;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        doTheStickyThing();
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    private void doTheStickyThing()
    {
        View viewThatShouldStick = null;
        View approachingView = null;
        for (View v : stickyViews)
        {
            int viewTop = getTopForViewRelativeOnlyChild(v) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop());
            if (viewTop <= 0)
            {
                if (viewThatShouldStick == null || viewTop > (getTopForViewRelativeOnlyChild(viewThatShouldStick) - getScrollY() + (clippingToPadding
                        ? 0 : getPaddingTop())))
                {
                    viewThatShouldStick = v;
                }
            }
            else
            {
                if (approachingView == null || viewTop < (getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0
                        : getPaddingTop())))
                {
                    approachingView = v;
                }
            }
        }
        if (viewThatShouldStick != null)
        {
            stickyViewTopOffset = approachingView == null ? 0 : Math.min(0,
                    getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop())
                            - viewThatShouldStick.getHeight());
            if (viewThatShouldStick != currentlyStickingView)
            {
                if (currentlyStickingView != null)
                {
                    stopStickingCurrentlyStickingView();
                }
                // only compute the left offset when we start sticking.
                stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(viewThatShouldStick);
                startStickingView(viewThatShouldStick);
            }
        }
        else if (currentlyStickingView != null)
        {
            stopStickingCurrentlyStickingView();
        }
    }

    private void startStickingView(View viewThatShouldStick)
    {
        currentlyStickingView = viewThatShouldStick;
        if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY))
        {
            hideView(currentlyStickingView);
        }
        if (((String) currentlyStickingView.getTag()).contains(FLAG_NONCONSTANT))
        {
            post(invalidateRunnable);
        }
    }

    private void stopStickingCurrentlyStickingView()
    {
        if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY))
        {
            showView(currentlyStickingView);
        }
        currentlyStickingView = null;
        removeCallbacks(invalidateRunnable);
    }

    /**
     * Notify that the sticky attribute has been added or removed from one or more views in the View hierarchy
     */
    public void notifyStickyAttributeChanged()
    {
        notifyHierarchyChanged();
    }

    private void notifyHierarchyChanged()
    {
        if (currentlyStickingView != null)
        {
            stopStickingCurrentlyStickingView();
        }
        stickyViews.clear();
        findStickyViews(getChildAt(0));
        doTheStickyThing();
        invalidate();
    }

    private void findStickyViews(View v)
    {
        if (v instanceof ViewGroup)
        {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++)
            {
                String tag = getStringTagForView(vg.getChildAt(i));
                if (tag != null && tag.contains(STICKY_TAG))
                {
                    stickyViews.add(vg.getChildAt(i));
                }
                else if (vg.getChildAt(i) instanceof ViewGroup)
                {
                    findStickyViews(vg.getChildAt(i));
                }
            }
        }
        else
        {
            String tag = (String) v.getTag();
            if (tag != null && tag.contains(STICKY_TAG))
            {
                stickyViews.add(v);
            }
        }
    }

    private String getStringTagForView(View v)
    {
        Object tagObject = v.getTag();
        return String.valueOf(tagObject);
    }

    private void hideView(View v)
    {
        if (SDKUtils.isHoneycombOrHigher())
        {
            v.setAlpha(0);
        }
        else
        {
            AlphaAnimation anim = new AlphaAnimation(1, 0);
            anim.setDuration(0);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }
    }

    private void showView(View v)
    {
        if (SDKUtils.isHoneycombOrHigher())
        {
            v.setAlpha(1);
        }
        else
        {
            AlphaAnimation anim = new AlphaAnimation(0, 1);
            anim.setDuration(0);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
            int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(
                deltaX,
                deltaY,
                scrollX,
                scrollY,
                scrollRangeX,
                scrollRangeY,
                mIsOverScrollEnabled ? maxOverScrollX : 0,
                mIsOverScrollEnabled ? maxOverScrollY : 0,
                isTouchEvent);
    }

    // region Helper Methods
    public void setOnScrollChangedListener(NotifyingScrollView.OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    public void setOverScrollEnabled(boolean enabled) {
        mIsOverScrollEnabled = enabled;
    }

    public boolean isOverScrollEnabled() {
        return mIsOverScrollEnabled;
    }
    // endregion
}