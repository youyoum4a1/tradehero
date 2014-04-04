package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionKey;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionAdapter extends ArrayDTOAdapter<DiscussionKey, SecurityDiscussionItemView>
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
