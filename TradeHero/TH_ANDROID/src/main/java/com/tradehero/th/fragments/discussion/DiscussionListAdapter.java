package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.AppendableArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 6:38 PM Copyright (c) TradeHero
 */
public class DiscussionListAdapter extends AppendableArrayDTOAdapter<DiscussionKey, DiscussionItemView>
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
