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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class DiscussionSetAdapter
        extends ViewDTOSetAdapter<DiscussionKey, AbstractDiscussionCompactItemViewLinear<DiscussionKey>>
{
    //<editor-fold desc="Constructors">
    public DiscussionSetAdapter(@NotNull Context context)
    {
        super(context);
    }

    public DiscussionSetAdapter(
            @NotNull Context context,
            @Nullable Collection<DiscussionKey> objects)
    {
        super(context, objects);
    }
    //</editor-fold>

    @Override @NotNull protected Set<DiscussionKey> createSet(@Nullable Collection<DiscussionKey> objects)
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
