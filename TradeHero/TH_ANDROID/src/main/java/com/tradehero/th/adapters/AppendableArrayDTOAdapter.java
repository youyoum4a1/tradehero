package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.DTOView;
import java.util.ArrayList;
import java.util.List;


public class AppendableArrayDTOAdapter<T, V extends DTOView<T>> extends ArrayDTOAdapter<T, V>
{
    public AppendableArrayDTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, T dto, V dtoView)
    {

    }

    public void appendMore(List<T> newItems)
    {
        List<T> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<T>();
        itemCopied.addAll(newItems);
        setItems(itemCopied);
        notifyDataSetChanged();
    }
}
