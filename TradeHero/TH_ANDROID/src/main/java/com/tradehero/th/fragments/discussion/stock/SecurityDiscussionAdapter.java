package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.AppendableArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.DiscussionKey;


public class SecurityDiscussionAdapter extends AppendableArrayDTOAdapter<DiscussionKey, SecurityDiscussionItemView>
{
    public SecurityDiscussionAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, DiscussionKey dto, SecurityDiscussionItemView dtoView)
    {
        // nothing for now
    }
}
