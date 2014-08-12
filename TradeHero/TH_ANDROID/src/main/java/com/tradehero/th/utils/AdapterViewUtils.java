package com.tradehero.th.utils;

import android.view.View;
import android.widget.AdapterView;
import com.android.internal.util.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class AdapterViewUtils
{
    @Inject public AdapterViewUtils()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public <T> boolean updateSingleRow(@NotNull AdapterView adapterView, @NotNull Class<T> type, @NotNull Predicate<T> comparator)
    {
        if (adapterView.getAdapter() != null)
        {
            int start = adapterView.getFirstVisiblePosition();
            for (int i = start, j = adapterView.getLastVisiblePosition(); i <= j; i++)
            {
                Object row = adapterView.getItemAtPosition(i);
                if (row != null && type.isInstance(row) && comparator.apply((T) row))
                {
                    View view = adapterView.getChildAt(i - start);
                    adapterView.getAdapter().getView(i, view, adapterView);
                    return true;
                }
            }
        }
        return false;
    }
}
