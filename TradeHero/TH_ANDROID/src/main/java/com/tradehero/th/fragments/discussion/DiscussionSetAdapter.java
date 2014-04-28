package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Collection;

abstract public class DiscussionSetAdapter
        extends ViewDTOSetAdapter<DiscussionKey, AbstractDiscussionItemView<DiscussionKey>>
{
    public DiscussionSetAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater);
    }

    public DiscussionSetAdapter(Context context, LayoutInflater inflater,
            Collection<DiscussionKey> objects)
    {
        super(context, inflater, objects);
    }
}
