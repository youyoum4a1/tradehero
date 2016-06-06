package com.androidth.general.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.androidth.general.api.DTOView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PagedDTOAdapterImpl<DTOType> extends ArrayAdapter<DTOType>
    implements PagedDTOAdapter<DTOType>
{
    protected static final int DEFAULT_VIEW_TYPE = 0;

    @NonNull protected final Map<Integer, List<DTOType>> pagedObjects;
    @LayoutRes protected int layoutResourceId;
    @NonNull protected LayoutInflater inflater;
    @NonNull private List<Integer> showedPageNum;

    //<editor-fold desc="Constructors">
    public PagedDTOAdapterImpl(@NonNull Context context, @LayoutRes int resource)
    {
        super(context, resource, new ArrayList<DTOType>());
        this.pagedObjects = new HashMap<>();
        this.layoutResourceId = resource;
        this.inflater = LayoutInflater.from(context);
        showedPageNum = new ArrayList<>();
    }
    //</editor-fold>

    @Override public void add(DTOType object)
    {
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    @Override public void addAll(DTOType... items)
    {
        throw new IllegalArgumentException();
    }

    @Override public void insert(DTOType object, int index)
    {
        throw new IllegalArgumentException();
    }

    @Override public void remove(DTOType object)
    {
        throw new IllegalArgumentException();
    }

    @Override public void clear()
    {
        pagedObjects.clear();
        rebuild();
        showedPageNum.clear();
    }

    @Override public void addPages(@NonNull Map<Integer, ? extends List<DTOType>> objects)
    {
        for (Map.Entry<Integer, ? extends List<DTOType>> entry : objects.entrySet())
        {
            pagedObjects.put(entry.getKey(), entry.getValue());
        }
        rebuild();
    }

    @Override public void addPage(int page, @NonNull List<DTOType> objects)
    {
        if (showedPageNum.contains(page))
        {
            return;
        }
        else
        {
            showedPageNum.add(page);
        }
        //save page
        pagedObjects.put(page, objects);
        //add new page
        rebuild();
        //notify
        notifyDataSetChanged();
        setNotifyOnChange(true);
    }

    protected void rebuild()
    {
        setNotifyOnChange(false);
        super.clear();
        super.addAll(makeItems());
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    @NonNull protected List<DTOType> makeItems()
    {
        List<DTOType> items = new ArrayList<>();
        for (Integer page : getPages())
        {
            items.addAll(pagedObjects.get(page));
        }
        return items;
    }

    @Override public boolean hasPage(int page)
    {
        List<Integer> pages = getPages();
        return pages.contains(page);
    }

    @Override @Nullable public List<DTOType> getPage(int page)
    {
        if (!getPages().contains(page))
        {
            return null;
        }
        List<DTOType> pageContent = pagedObjects.get(page);
        if (pageContent == null)
        {
            return null;
        }
        return new ArrayList<>(pageContent);
    }

    @NonNull protected List<Integer> getPages()
    {
        // Get the shownPages ordered
        //noinspection Convert2Diamond
        Set<Integer> pages = new TreeSet<Integer>(new Comparator<Integer>()
        {
            @Override public int compare(Integer lhs, Integer rhs)
            {
                return lhs.compareTo(rhs);
            }
        });
        pages.addAll(pagedObjects.keySet());
        List<Integer> contiguousPages = new LinkedList<>();
        Integer currentPage = null;
        for (Integer page : pages)
        {
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

    @Override @Nullable public Integer getLatestPage()
    {
        List<Integer> pages = getPages();
        int size = pages.size();
        if (size == 0)
        {
            return null;
        }
        return pages.get(size - 1);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflate(position, viewGroup);
        }

        //noinspection unchecked
        DTOView<DTOType> dtoView = (DTOView<DTOType>) convertView;
        dtoView.display(getItem(position));
        return convertView;
    }

    @NonNull protected View inflate(int position, ViewGroup viewGroup)
    {
        return inflater.inflate(getViewResId(position), viewGroup, false);
    }

    public void setLayoutResourceId(@LayoutRes int layoutResourceId)
    {
        this.layoutResourceId = layoutResourceId;
    }

    @Override public int getViewTypeCount()
    {
        return 1;
    }

    @Override public int getItemViewType(int position)
    {
        return DEFAULT_VIEW_TYPE;
    }

    @LayoutRes public int getViewResId(@SuppressWarnings("UnusedParameters") int position)
    {
        return layoutResourceId;
    }
}
