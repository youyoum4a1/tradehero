package com.tradehero.th.fragments.base;

import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.th.fragments.MovableBottom;

public interface FragmentOuterElements
{
    @NonNull AbsListView.OnScrollListener getListViewScrollListener();

    @NonNull NotifyingScrollView.OnScrollChangedListener getScrollViewListener();

    @NonNull MovableBottom getMovableBottom();

    boolean onOptionItemsSelected(MenuItem item);
}
