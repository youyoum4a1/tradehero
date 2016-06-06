package com.androidth.general.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListAdapter;
import java.util.List;
import java.util.Map;

public interface PagedDTOAdapter<DTOType> extends ListAdapter
{
    void clear();
    void addPages(@NonNull Map<Integer, ? extends List<DTOType>> objects);
    void addPage(int page, @NonNull List<DTOType> objects);
    boolean hasPage(int page);
    @Nullable List<DTOType> getPage(int page);
    @Nullable Integer getLatestPage();
}
