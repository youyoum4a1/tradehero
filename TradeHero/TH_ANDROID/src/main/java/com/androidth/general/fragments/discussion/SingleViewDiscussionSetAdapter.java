package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public class SingleViewDiscussionSetAdapter extends DiscussionSetAdapter
{
    @LayoutRes public final int layoutResId;

    //<editor-fold desc="Constructors">
    public SingleViewDiscussionSetAdapter(
            @NonNull Context context,
            @LayoutRes int layoutResId)
    {
        super(context);
        this.layoutResId = layoutResId;
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResId;
    }
}
