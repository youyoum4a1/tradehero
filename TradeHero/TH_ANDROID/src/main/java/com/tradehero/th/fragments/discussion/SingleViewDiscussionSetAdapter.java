package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;

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

    public SingleViewDiscussionSetAdapter(
            @NonNull Context context,
            @Nullable Collection<AbstractDiscussionCompactItemViewLinear.DTO> objects,
            @LayoutRes int layoutResId)
    {
        super(context, objects);
        this.layoutResId = layoutResId;
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResId;
    }
}
