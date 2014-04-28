package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class PrivateDiscussionSetAdapter extends DiscussionSetAdapter
{
    public static final int ITEM_TYPE_MINE = 0;
    public static final int ITEM_TYPE_OTHER = 1;

    public final int mineResId;
    public final int otherResId;

    @Inject DiscussionCache discussionCache;
    @Inject CurrentUserId currentUserId;

    public PrivateDiscussionSetAdapter(Context context, LayoutInflater inflater, int mineResId, int otherResId)
    {
        super(context, inflater);
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
        return isMine(position) ? ITEM_TYPE_MINE : ITEM_TYPE_OTHER;
    }

    @Override protected int getViewResId(int position)
    {
        switch(getItemViewType(position))
        {
            case ITEM_TYPE_MINE:
                return mineResId;
            case ITEM_TYPE_OTHER:
                return otherResId;
        }
        throw new IllegalArgumentException("Unknown type at position " + position);
    }

    protected boolean isMine(int position)
    {
        return isMine(discussionCache.get(getItem(position)));
    }

    protected boolean isMine(AbstractDiscussionDTO discussionDTO)
    {
        return discussionDTO == null ||
                (currentUserId.toUserBaseKey().key.equals(discussionDTO.userId));
    }
}
