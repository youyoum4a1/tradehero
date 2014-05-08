package com.tradehero.th.fragments.discussion;

import android.content.Context;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Collection;

public class SingleViewDiscussionSetAdapter extends DiscussionSetAdapter
{
    public final int layoutResId;

    public SingleViewDiscussionSetAdapter(Context context, int layoutResId)
    {
        super(context);
        this.layoutResId = layoutResId;
    }

    public SingleViewDiscussionSetAdapter(Context context,
            Collection<DiscussionKey> objects, int layoutResId)
    {
        super(context, objects);
        this.layoutResId = layoutResId;
    }

    @Override protected int getViewResId(int position)
    {
        return layoutResId;
    }
}
