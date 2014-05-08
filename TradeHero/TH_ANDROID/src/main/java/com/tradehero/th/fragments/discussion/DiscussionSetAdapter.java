package com.tradehero.th.fragments.discussion;

import android.content.Context;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyComparatorIdDesc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

abstract public class DiscussionSetAdapter
        extends ViewDTOSetAdapter<DiscussionKey, AbstractDiscussionItemView<DiscussionKey>>
{
    public DiscussionSetAdapter(Context context)
    {
        super(context);
    }

    public DiscussionSetAdapter(Context context,
            Collection<DiscussionKey> objects)
    {
        super(context, objects);
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

    public void appendTail(DiscussionDTO newElement)
    {
        if (newElement != null)
        {
            if (newElement.stubKey != null)
            {
                set.remove(newElement.stubKey);
            }
            List<DiscussionKey> toAdd = new ArrayList<>();
            toAdd.add(newElement.getDiscussionKey());
            appendTail(toAdd);
        }
    }
}
