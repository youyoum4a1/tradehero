package com.tradehero.th.widget;

import android.widget.AbsListView;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.FragmentOuterElements;

public class LiveWidgetScrollListener implements AbsListView.OnScrollListener
{
    private FragmentOuterElements fragmentOuterElements;
    private BaseLiveFragmentUtil liveFragmentUtil;

    public LiveWidgetScrollListener(FragmentOuterElements fragmentOuterElements, BaseLiveFragmentUtil liveFragmentUtil)
    {
        this.fragmentOuterElements = fragmentOuterElements;
        this.liveFragmentUtil = liveFragmentUtil;
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        float y = fragmentOuterElements.getMovableBottom().getTranslationY();
        liveFragmentUtil.setLiveWidgetTranslationY(y);
    }
}
