package com.tradehero.th.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import java.util.LinkedList;
import java.util.List;

public abstract class PagedRecyclerAdapter<T> extends TypedRecyclerAdapter<T>
{
    SparseArray<Integer> shownPages = new SparseArray<>();

    public PagedRecyclerAdapter(Class<T> klass, @NonNull TypedRecyclerComparator<T> comparator)
    {
        super(klass, comparator);
    }

    @NonNull protected List<Integer> getShownPages()
    {
        List<Integer> contiguousPages = new LinkedList<>();

        Integer currentPage = null;
        for (int i = 0 ; i < shownPages.size(); i++)
        {
            Integer page = shownPages.keyAt(i);
            if (currentPage != null && !page.equals(currentPage + 1))
            {
                // We stop at a gap in the page numbering
                break;
            }
            currentPage = page;
            contiguousPages.add(currentPage);
        }
        return contiguousPages;
    }

    @Nullable public Integer getLatestPage()
    {
        List<Integer> pages = getShownPages();
        int size = pages.size();
        if (size == 0)
        {
            return null;
        }
        return pages.get(size - 1);
    }

    public int getPageSize(int page)
    {
        return shownPages.get(page, 0);
    }

    public void addPage(int page, @NonNull List<T> objects)
    {
        shownPages.append(page, objects.size());
        addAll(objects);
    }

    public void clear()
    {
        super.removeAll();
        shownPages.clear();
    }
}
