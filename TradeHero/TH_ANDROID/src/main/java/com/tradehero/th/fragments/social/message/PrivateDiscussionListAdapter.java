package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class PrivateDiscussionListAdapter extends DiscussionListAdapter
{
    public static final int ITEM_TYPE_MINE = 0;
    public static final int ITEM_TYPE_OTHER = 1;

    public final int mineResId;
    public final int otherResId;

    @Inject DiscussionCache discussionCache;
    @Inject CurrentUserId currentUserId;

    public PrivateDiscussionListAdapter(
            Context context,
            LayoutInflater inflater,
            int mineResId, int otherResId)
    {
        super(context, inflater, 0);
        this.mineResId = mineResId;
        this.otherResId = otherResId;
        DaggerUtils.inject(this);
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return isMine(discussionCache.get((DiscussionKey) getItem(position))) ? ITEM_TYPE_MINE
                : ITEM_TYPE_OTHER;
    }

    protected int getItemViewResId(int position)
    {
        return isMine(discussionCache.get((DiscussionKey) getItem(position))) ? mineResId
                : otherResId;
    }

    protected boolean isMine(AbstractDiscussionDTO discussionDTO)
    {

        if (discussionDTO == null)
        {
            return true;
        }
        else
        {
            return discussionDTO != null && currentUserId.toUserBaseKey().key.equals(
                    discussionDTO.userId);
        }
    }

    @Override protected View conditionalInflate(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(getItemViewResId(position), viewGroup, false);
        }
        return convertView;
    }
}
