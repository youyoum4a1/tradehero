package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.discussion.AbstractDiscussionDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.persistence.discussion.DiscussionCacheRx;

public class PrivateDiscussionSetAdapter extends DiscussionSetAdapter
{
    public static final int ITEM_TYPE_MINE = 0;
    public static final int ITEM_TYPE_OTHER = 1;

    @LayoutRes public final int mineResId;
    @LayoutRes public final int otherResId;

    @NonNull DiscussionCacheRx discussionCache;
    @NonNull CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public PrivateDiscussionSetAdapter(
            @NonNull Context context,
            @NonNull DiscussionCacheRx discussionCache,
            @NonNull CurrentUserId currentUserId,
            @LayoutRes int mineResId,
            @LayoutRes int otherResId)
    {
        super(context, new AbstractDiscussionCompactItemViewDTODateComparator(false));
        this.discussionCache = discussionCache;
        this.currentUserId = currentUserId;
        this.mineResId = mineResId;
        this.otherResId = otherResId;
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return isMine(position) ? ITEM_TYPE_MINE : ITEM_TYPE_OTHER;
    }

    @Override @LayoutRes protected int getViewResId(int position)
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
        return isMine((AbstractDiscussionDTO) getItem(position).viewHolderDTO.discussionDTO);
    }

    protected boolean isMine(@Nullable AbstractDiscussionDTO discussionDTO)
    {
        return discussionDTO == null ||
                (currentUserId.toUserBaseKey().key.equals(discussionDTO.userId));
    }
}
