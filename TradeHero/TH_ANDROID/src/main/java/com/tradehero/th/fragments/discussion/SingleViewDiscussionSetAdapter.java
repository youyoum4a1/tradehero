package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.LayoutRes;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleViewDiscussionSetAdapter extends DiscussionSetAdapter
{
    @LayoutRes public final int layoutResId;

    //<editor-fold desc="Constructors">
    public SingleViewDiscussionSetAdapter(
            @NotNull Context context,
            @LayoutRes int layoutResId)
    {
        super(context);
        this.layoutResId = layoutResId;
    }

    public SingleViewDiscussionSetAdapter(
            @NotNull Context context,
            @Nullable Collection<DiscussionKey> objects,
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
