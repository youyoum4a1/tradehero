package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.IndicatorLayout;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/** Created with IntelliJ IDEA. User: tho Date: 9/13/13 Time: 11:35 AM Copyright (c) TradeHero */
public class PullToRefreshStickyListHeadersListView extends PullToRefreshBase<StickyListHeadersListView>
        implements AbsListView.OnScrollListener
{
    private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp)
    {
        FrameLayout.LayoutParams newLp = null;

        if (null != lp)
        {
            newLp = new FrameLayout.LayoutParams(lp);

            if (lp instanceof LinearLayout.LayoutParams)
            {
                newLp.gravity = ((LinearLayout.LayoutParams) lp).gravity;
            }
            else
            {
                newLp.gravity = Gravity.CENTER;
            }
        }

        return newLp;
    }

    private boolean mLastItemVisible;
    private AbsListView.OnScrollListener mOnScrollListener;
    private OnLastItemVisibleListener mOnLastItemVisibleListener;
    private View mEmptyView;

    private IndicatorLayout mIndicatorIvTop;
    private IndicatorLayout mIndicatorIvBottom;

    private boolean mShowIndicator;
    private boolean mScrollEmptyView = true;

    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;

    private FrameLayout mLvFooterLoadingFrame;

    private boolean mListViewExtrasEnabled;

    //<editor-fold desc="Constructors">
    public PullToRefreshStickyListHeadersListView(Context context)
    {
        super(context);
        mRefreshableView.setOnScrollListener(this);
    }

    public PullToRefreshStickyListHeadersListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mRefreshableView.setOnScrollListener(this);
    }

    public PullToRefreshStickyListHeadersListView(Context context, Mode mode)
    {
        super(context, mode);
        mRefreshableView.setOnScrollListener(this);
    }

    public PullToRefreshStickyListHeadersListView(Context context, Mode mode, AnimationStyle animStyle)
    {
        super(context, mode, animStyle);
        mRefreshableView.setOnScrollListener(this);
    }
    //</editor-fold>

    @Override public final Orientation getPullToRefreshScrollDirection()
    {
        return Orientation.VERTICAL;
    }

    @Override protected void onRefreshing(final boolean doScroll)
    {
        /**
         * If we're not showing the Refreshing view, or the list is empty, the
         * the header/footer views won't show so we use the normal method.
         */
        ListAdapter adapter = mRefreshableView.getAdapter();
        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty())
        {
            superOnRefreshing(doScroll);
            return;
        }

        superOnRefreshing(false);

        final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
        final int selection, scrollToY;

        switch (getCurrentMode())
        {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                origLoadingView = getFooterLayout();
                listViewLoadingView = mFooterLoadingView;
                oppositeListViewLoadingView = mHeaderLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();
                break;
            case PULL_FROM_START:
            default:
                origLoadingView = getHeaderLayout();
                listViewLoadingView = mHeaderLoadingView;
                oppositeListViewLoadingView = mFooterLoadingView;
                selection = 0;
                scrollToY = getScrollY() + getHeaderSize();
                break;
        }

        // Hide our original Loading View
        origLoadingView.reset();
        origLoadingView.hideAllViews();

        // Make sure the opposite end is hidden too
        oppositeListViewLoadingView.setVisibility(View.GONE);

        // Show the ListView Loading View and set it to refresh.
        listViewLoadingView.setVisibility(View.VISIBLE);
        listViewLoadingView.refreshing();

        if (doScroll)
        {
            // We need to disable the automatic visibility changes for now
            disableLoadingLayoutVisibilityChanges();

            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);

            // Make sure the ListView is scrolled to show the loading
            // header/footer
            mRefreshableView.setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }
    }

    @Override protected void onReset()
    {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        if (!mListViewExtrasEnabled)
        {
            superOnReset();
            return;
        }

        final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode())
        {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToHeight = getFooterSize();
                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
                break;
            case PULL_FROM_START:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                scrollToHeight = -getHeaderSize();
                selection = 0;
                scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
                break;
        }

        // If the ListView header loading layout is showing, then we need to
        // flip so that the original one is showing instead
        if (listViewLoadingLayout.getVisibility() == View.VISIBLE)
        {

            // Set our Original View to Visible
            originalLoadingLayout.showInvisibleViews();

            // Hide the ListView Header/Footer
            listViewLoadingLayout.setVisibility(View.GONE);

            /**
             * Scroll so the View is at the same Y as the ListView
             * header/footer, but only scroll if: we've pulled to refresh, it's
             * positioned correctly
             */
            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING)
            {
                mRefreshableView.setSelection(selection);
                setHeaderScroll(scrollToHeight);
            }
        }

        // Finally, call up to super
        superOnReset();
    }

    @Override protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd)
    {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

        if (mListViewExtrasEnabled)
        {
            final Mode mode = getMode();

            if (includeStart && mode.showHeaderLoadingLayout())
            {
                proxy.addLayout(mHeaderLoadingView);
            }
            if (includeEnd && mode.showFooterLoadingLayout())
            {
                proxy.addLayout(mFooterLoadingView);
            }
        }

        return proxy;
    }

    protected StickyListHeadersListView createListView(Context context, AttributeSet attrs)
    {
        final StickyListHeadersListView lv;
        lv = new InternalListView(context, attrs);
        return lv;
    }

    @Override protected StickyListHeadersListView createRefreshableView(Context context, AttributeSet attrs)
    {
        StickyListHeadersListView lv = createListView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    @Override protected void handleStyledAttributes(TypedArray a)
    {
        // Set Show Indicator to the XML value, or default value
        mShowIndicator = a.getBoolean(R.styleable.PullToRefresh_ptrShowIndicator, !isPullToRefreshOverScrollEnabled());

        mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

        if (mListViewExtrasEnabled)
        {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

            // Create Loading Views ready for use later
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
            mHeaderLoadingView.setVisibility(View.GONE);
            frame.addView(mHeaderLoadingView, lp);
            mRefreshableView.addHeaderView(frame, null, false);

            mLvFooterLoadingFrame = new FrameLayout(getContext());
            mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
            mFooterLoadingView.setVisibility(View.GONE);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

            /**
             * If the value for Scrolling While Refreshing hasn't been
             * explicitly set via XML, enable Scrolling While Refreshing.
             */
            if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled))
            {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }

    protected class InternalListView extends StickyListHeadersListView implements EmptyViewMethodAccessor
    {
        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override protected void dispatchDraw(Canvas canvas)
        {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try
            {
                super.dispatchDraw(canvas);
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }

        @Override public boolean dispatchTouchEvent(MotionEvent ev)
        {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try
            {
                return super.dispatchTouchEvent(ev);
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
                return false;
            }
        }

        @Override public void setAdapter(StickyListHeadersAdapter adapter)
        {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter)
            {
                addFooterView(mLvFooterLoadingFrame);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override public void setEmptyView(View emptyView)
        {
            PullToRefreshStickyListHeadersListView.this.setEmptyView(emptyView);
        }

        @Override public void setEmptyViewInternal(View emptyView)
        {
            super.setEmptyView(emptyView);
        }
    }

    /**
     * Gets whether an indicator graphic should be displayed when the View is in a state where a Pull-to-Refresh can happen. An example of this state
     * is when the Adapter View is scrolled to the top and the mode is set to {@link Mode#PULL_FROM_START}. The default value is <var>true</var> if
     * {@link PullToRefreshBase#isPullToRefreshOverScrollEnabled() isPullToRefreshOverScrollEnabled()} returns false.
     *
     * @return true if the indicators will be shown
     */
    public boolean getShowIndicator()
    {
        return mShowIndicator;
    }

    public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
            final int totalItemCount)
    {

        if (DEBUG)
        {
            Log.d(LOG_TAG, "First Visible: " + firstVisibleItem + ". Visible Count: " + visibleItemCount
                    + ". Total Items:" + totalItemCount);
        }

        /**
         * Set whether the Last Item is Visible. lastVisibleItemIndex is a
         * zero-based index, so we minus one totalItemCount to check
         */
        if (null != mOnLastItemVisibleListener)
        {
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        }

        // If we're showing the indicator, check positions...
        if (getShowIndicatorInternal())
        {
            updateIndicatorViewsVisibility();
        }

        // Finally call OnScrollListener if we have one
        if (null != mOnScrollListener)
        {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public final void onScrollStateChanged(final AbsListView view, final int state)
    {
        /**
         * Check that the scrolling has stopped, and that the last item is
         * visible.
         */
        if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible)
        {
            mOnLastItemVisibleListener.onLastItemVisible();
        }

        if (null != mOnScrollListener)
        {
            mOnScrollListener.onScrollStateChanged(view, state);
        }
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView() getRefreshableView()}. {@link
     * android.widget.AdapterView#setAdapter(android.widget.Adapter)} setAdapter(adapter)}. This is just for convenience!
     *
     * @param adapter - Adapter to set
     */
    public void setAdapter(StickyListHeadersAdapter adapter)
    {
        mRefreshableView.setAdapter(adapter);
    }

    public StickyListHeadersAdapter getAdapter()
    {
        if (mRefreshableView == null)
        {
            return null;
        }
        return mRefreshableView.getAdapter();
    }

    /**
     * Sets the Empty View to be used by the Adapter View. <p/> We need it handle it ourselves so that we can Pull-to-Refresh when the Empty View is
     * shown. <p/> Please note, you do <strong>not</strong> usually need to call this method yourself. Calling setEmptyView on the AdapterView will
     * automatically call this method and set everything up. This includes when the Android Framework automatically sets the Empty View based on it's
     * ID.
     *
     * @param newEmptyView - Empty View to be used
     */
    public final void setEmptyView(View newEmptyView)
    {
        FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();

        // If we already have an Empty View, remove it
        if (null != mEmptyView)
        {
            refreshableViewWrapper.removeView(mEmptyView);
        }

        if (null != newEmptyView)
        {
            // New view needs to be clickable so that Android recognizes it as a
            // target for Touch Events
            newEmptyView.setClickable(true);

            ViewParent newEmptyViewParent = newEmptyView.getParent();
            if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup)
            {
                ((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
            }

            // We need to convert any LayoutParams so that it works in our
            // FrameLayout
            FrameLayout.LayoutParams lp = convertEmptyViewLayoutParams(newEmptyView.getLayoutParams());
            if (null != lp)
            {
                refreshableViewWrapper.addView(newEmptyView, lp);
            }
            else
            {
                refreshableViewWrapper.addView(newEmptyView);
            }
        }

        if (mRefreshableView instanceof EmptyViewMethodAccessor)
        {
            ((EmptyViewMethodAccessor) mRefreshableView).setEmptyViewInternal(newEmptyView);
        }
        else
        {
            mRefreshableView.setEmptyView(newEmptyView);
        }
        mEmptyView = newEmptyView;
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView() getRefreshableView()}. {@link
     * AdapterView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener) setOnItemClickListener(listener)}. This is just for
     * convenience!
     *
     * @param listener - OnItemClickListener to use
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        mRefreshableView.setOnItemClickListener(listener);
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener)
    {
        mOnLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(AbsListView.OnScrollListener listener)
    {
        mOnScrollListener = listener;
    }

    public final void setScrollEmptyView(boolean doScroll)
    {
        mScrollEmptyView = doScroll;
    }

    /**
     * Sets whether an indicator graphic should be displayed when the View is in a state where a Pull-to-Refresh can happen. An example of this state
     * is when the Adapter View is scrolled to the top and the mode is set to {@link Mode#PULL_FROM_START}
     *
     * @param showIndicator - true if the indicators should be shown.
     */
    public void setShowIndicator(boolean showIndicator)
    {
        mShowIndicator = showIndicator;

        if (getShowIndicatorInternal())
        {
            // If we're set to Show Indicator, add/update them
            addIndicatorViews();
        }
        else
        {
            // If not, then remove then
            removeIndicatorViews();
        }
    }

    @Override protected void onPullToRefresh()
    {
        super.onPullToRefresh();

        if (getShowIndicatorInternal())
        {
            switch (getCurrentMode())
            {
                case PULL_FROM_END:
                    mIndicatorIvBottom.pullToRefresh();
                    break;
                case PULL_FROM_START:
                    mIndicatorIvTop.pullToRefresh();
                    break;
                default:
                    // NO-OP
                    break;
            }
        }
    }

    protected void superOnRefreshing(boolean doScroll)
    {
        super.onRefreshing(doScroll);

        if (getShowIndicatorInternal())
        {
            updateIndicatorViewsVisibility();
        }
    }

    @Override protected void onReleaseToRefresh()
    {
        super.onReleaseToRefresh();

        if (getShowIndicatorInternal())
        {
            switch (getCurrentMode())
            {
                case PULL_FROM_END:
                    mIndicatorIvBottom.releaseToRefresh();
                    break;
                case PULL_FROM_START:
                    mIndicatorIvTop.releaseToRefresh();
                    break;
                default:
                    // NO-OP
                    break;
            }
        }
    }

    protected void superOnReset()
    {
        super.onReset();

        if (getShowIndicatorInternal())
        {
            updateIndicatorViewsVisibility();
        }
    }

    protected boolean isReadyForPullStart()
    {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullEnd()
    {
        return isLastItemVisible();
    }

    @Override protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != mEmptyView && !mScrollEmptyView)
        {
            mEmptyView.scrollTo(-l, -t);
        }
    }

    @Override protected void updateUIForMode()
    {
        super.updateUIForMode();

        // Check Indicator Views consistent with new Mode
        if (getShowIndicatorInternal())
        {
            addIndicatorViews();
        }
        else
        {
            removeIndicatorViews();
        }
    }

    private void addIndicatorViews()
    {
        Mode mode = getMode();
        FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();

        if (mode.showHeaderLoadingLayout() && null == mIndicatorIvTop)
        {
            // If the mode can pull down, and we don't have one set already
            mIndicatorIvTop = new IndicatorLayout(getContext(), Mode.PULL_FROM_START);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.indicator_right_padding);
            params.gravity = Gravity.TOP | Gravity.RIGHT;
            refreshableViewWrapper.addView(mIndicatorIvTop, params);
        }
        else if (!mode.showHeaderLoadingLayout() && null != mIndicatorIvTop)
        {
            // If we can't pull down, but have a View then remove it
            refreshableViewWrapper.removeView(mIndicatorIvTop);
            mIndicatorIvTop = null;
        }

        if (mode.showFooterLoadingLayout() && null == mIndicatorIvBottom)
        {
            // If the mode can pull down, and we don't have one set already
            mIndicatorIvBottom = new IndicatorLayout(getContext(), Mode.PULL_FROM_END);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.indicator_right_padding);
            params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            refreshableViewWrapper.addView(mIndicatorIvBottom, params);
        }
        else if (!mode.showFooterLoadingLayout() && null != mIndicatorIvBottom)
        {
            // If we can't pull down, but have a View then remove it
            refreshableViewWrapper.removeView(mIndicatorIvBottom);
            mIndicatorIvBottom = null;
        }
    }

    private boolean getShowIndicatorInternal()
    {
        return mShowIndicator && isPullToRefreshEnabled();
    }

    private boolean isFirstItemVisible()
    {
        final Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty())
        {
            if (DEBUG)
            {
                Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
            }
            return true;
        }
        else
        {

            /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRefreshableView.getFirstVisiblePosition() <= 1)
            {
                final View firstVisibleChild = mRefreshableView.getChildAt(0);
                if (firstVisibleChild != null)
                {
                    return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }

    private boolean isLastItemVisible()
    {
        final Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty())
        {
            if (DEBUG)
            {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return true;
        }
        else
        {
            final int lastItemPosition = mRefreshableView.getCount() - 1;
            final int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();

            if (DEBUG)
            {
                Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
                        + lastVisiblePosition);
            }

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1)
            {
                final int childIndex = lastVisiblePosition - mRefreshableView.getFirstVisiblePosition();
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null)
                {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }

        return false;
    }

    private void removeIndicatorViews()
    {
        if (null != mIndicatorIvTop)
        {
            getRefreshableViewWrapper().removeView(mIndicatorIvTop);
            mIndicatorIvTop = null;
        }

        if (null != mIndicatorIvBottom)
        {
            getRefreshableViewWrapper().removeView(mIndicatorIvBottom);
            mIndicatorIvBottom = null;
        }
    }

    private void updateIndicatorViewsVisibility()
    {
        if (null != mIndicatorIvTop)
        {
            if (!isRefreshing() && isReadyForPullStart())
            {
                if (!mIndicatorIvTop.isVisible())
                {
                    mIndicatorIvTop.show();
                }
            }
            else
            {
                if (mIndicatorIvTop.isVisible())
                {
                    mIndicatorIvTop.hide();
                }
            }
        }

        if (null != mIndicatorIvBottom)
        {
            if (!isRefreshing() && isReadyForPullEnd())
            {
                if (!mIndicatorIvBottom.isVisible())
                {
                    mIndicatorIvBottom.show();
                }
            }
            else
            {
                if (mIndicatorIvBottom.isVisible())
                {
                    mIndicatorIvBottom.hide();
                }
            }
        }
    }
}
