package com.ayondo.academy.fragments.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.ayondo.academy.fragments.MovableBottom;

public interface FragmentOuterElements
{
    @NonNull AbsListView.OnScrollListener getListViewScrollListener();

    @NonNull NotifyingScrollView.OnScrollChangedListener getScrollViewListener();

    @NonNull RecyclerView.OnScrollListener getRecyclerViewScrollListener();

    @NonNull MovableBottom getMovableBottom();

    boolean onOptionItemsSelected(MenuItem item);
}
