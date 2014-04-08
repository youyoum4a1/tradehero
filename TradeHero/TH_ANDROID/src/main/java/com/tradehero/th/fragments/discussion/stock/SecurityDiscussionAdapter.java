package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionKey;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import java.util.ArrayList;
import java.util.List;

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

    public void appendMore(DiscussionKeyList discussionKeyList)
    {
        List<DiscussionKey> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<DiscussionKey>();
        itemCopied.addAll(discussionKeyList);
        setItems(itemCopied);
        notifyDataSetChanged();
    }
}
