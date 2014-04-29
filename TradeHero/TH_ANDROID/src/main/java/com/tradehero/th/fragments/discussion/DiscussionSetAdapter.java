package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyComparatorIdDesc;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

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

    @Override protected Set<DiscussionKey> createSet(Collection<DiscussionKey> objects)
    {
        Set<DiscussionKey> created = new TreeSet<>(new DiscussionKeyComparatorIdDesc());
        if (objects != null)
        {
            created.addAll(objects);
        }
        return created;
    }
}
