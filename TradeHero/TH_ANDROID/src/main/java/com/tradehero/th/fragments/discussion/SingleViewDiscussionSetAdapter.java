package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Collection;

public class SingleViewDiscussionSetAdapter extends DiscussionSetAdapter
{
    public final int layoutResId;

    public SingleViewDiscussionSetAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater);
        this.layoutResId = layoutResId;
    }

    public SingleViewDiscussionSetAdapter(Context context, LayoutInflater inflater,
            Collection<DiscussionKey> objects, int layoutResId)
    {
        super(context, inflater, objects);
        this.layoutResId = layoutResId;
    }

    @Override protected int getViewResId(int position)
    {
        return layoutResId;
    }
}
