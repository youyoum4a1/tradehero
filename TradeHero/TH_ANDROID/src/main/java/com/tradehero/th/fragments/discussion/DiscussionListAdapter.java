package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.AppendableArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.DiscussionKey;

public class DiscussionListAdapter extends AppendableArrayDTOAdapter<DiscussionKey, AbstractDiscussionItemView<DiscussionKey>>
{
    public DiscussionListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }
}
