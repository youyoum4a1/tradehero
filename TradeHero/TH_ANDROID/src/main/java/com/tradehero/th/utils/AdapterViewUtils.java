package com.tradehero.th.utils;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import com.android.internal.util.Predicate;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class AdapterViewUtils
{
    //<editor-fold desc="Constructors">
    @Inject public AdapterViewUtils()
    {
        super();
    }
    //</editor-fold>

    public <T> boolean updateSingleRow(@NotNull AdapterView adapterView, @NotNull Class<T> type, @NotNull Predicate<T> predicate)
    {
        Adapter adapter = adapterView.getAdapter();
        if (adapter != null)
        {
            int start = adapterView.getFirstVisiblePosition();
            for (int i = start, j = adapterView.getLastVisiblePosition(); i <= j; i++)
            {
                Object row = adapterView.getItemAtPosition(i);
                //noinspection unchecked
                if (row != null && type.isInstance(row) && predicate.apply((T) row))
                {
                    View view = adapterView.getChildAt(i - start);
                    adapter.getView(i, view, adapterView);
                    return true;
                }
            }
        }
        return false;
    }
}
