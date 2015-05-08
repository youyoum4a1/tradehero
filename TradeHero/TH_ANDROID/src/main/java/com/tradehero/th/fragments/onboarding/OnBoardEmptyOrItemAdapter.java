package com.tradehero.th.fragments.onboarding;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.DTOAdapterNew;

public class OnBoardEmptyOrItemAdapter<T> extends DTOAdapterNew<T>
{
    private static final int VIEW_TYPE_EMPTY = DEFAULT_VIEW_TYPE + 1;

    @LayoutRes private final int emptyRes;

    //<editor-fold desc="Constructors">
    public OnBoardEmptyOrItemAdapter(@NonNull Context context,
            @LayoutRes int exchangeResId,
            @LayoutRes int emptyRes)
    {
        super(context, exchangeResId);
        this.emptyRes = emptyRes;
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return super.getViewTypeCount() + 1;
    }

    @Override public int getItemViewType(int position)
    {
        if (super.getCount() == 0)
        {
            return VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (super.getCount() == 0)
        {
            return inflater.inflate(emptyRes, viewGroup, false);
        }
        else
        {
            return super.getView(position, convertView, viewGroup);
        }
    }

    @Override public int getViewResId(@SuppressWarnings("UnusedParameters") int position)
    {
        if (super.getCount() == 0)
        {
            return emptyRes;
        }
        return super.getViewResId(position);
    }

    @Override public int getCount()
    {
        return Math.max(1, super.getCount());
    }

    @Override public T getItem(int position)
    {
        if (super.getCount() == 0)
        {
            return null;
        }
        return super.getItem(position);
    }

    @Override public boolean isEnabled(int position)
    {
        return super.getCount() > 0;
    }
}
