package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyComparatorIdAsc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

abstract public class DiscussionSetAdapter
        extends ViewDTOSetAdapter<DiscussionKey, AbstractDiscussionCompactItemViewLinear<DiscussionKey>>
{
    //<editor-fold desc="Constructors">
    public DiscussionSetAdapter(@NonNull Context context)
    {
        super(context, new DiscussionKeyComparatorIdAsc());
    }

    public DiscussionSetAdapter(@NonNull Context context, @Nullable Comparator<DiscussionKey> comparator)
    {
        super(context, comparator);
    }

    public DiscussionSetAdapter(
            @NonNull Context context,
            @Nullable Collection<DiscussionKey> objects)
    {
        super(context, new DiscussionKeyComparatorIdAsc(), objects);
    }

    public DiscussionSetAdapter(@NonNull Context context, @Nullable Comparator<DiscussionKey> comparator, @Nullable Collection<DiscussionKey> objects)
    {
        super(context, comparator, objects);
    }
    //</editor-fold>

    public void appendTail(@Nullable DiscussionDTO newElement)
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
