package com.tradehero.th.utils;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import com.android.internal.util.Predicate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class AdapterViewUtils
{
    //<editor-fold desc="Constructors">
    @Inject public AdapterViewUtils()
    {
        super();
    }
    //</editor-fold>

    public <T> boolean updateSingleRowWhere(@NotNull AdapterView adapterView, @NotNull Class<T> type, @NotNull Predicate<T> predicate)
    {
        return updateRowsWhere(adapterView, type, predicate, 1) == 1;
    }

    public <T> int updateRowsWhere(@NotNull AdapterView adapterView, @NotNull Class<T> type, @NotNull Predicate<T> predicate, int maxCount)
    {
        if (maxCount < 1)
        {
            throw new IllegalArgumentException("maxCount cannot be " + maxCount);
        }
        int changedCount = 0;
        Adapter adapter = adapterView.getAdapter();
        if (adapter != null)
        {
            int start = adapterView.getFirstVisiblePosition();
            for (int i = start, j = adapterView.getLastVisiblePosition(); i <= j; i++)
            {
                Object row = adapterView.getItemAtPosition(i);
                //noinspection unchecked
                if (maxCount > 0 && row != null && type.isInstance(row) && predicate.apply((T) row))
                {
                    View view = adapterView.getChildAt(i - start);
                    adapter.getView(i, view, adapterView);
                    maxCount--;
                    changedCount++;
                }
            }
        }
        return changedCount;
    }
}
