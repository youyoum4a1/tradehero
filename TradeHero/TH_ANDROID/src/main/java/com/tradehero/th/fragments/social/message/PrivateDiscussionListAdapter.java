package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import javax.inject.Inject;

public class PrivateDiscussionListAdapter extends DiscussionListAdapter
{
    public static final int ITEM_TYPE_MINE = 0;
    public static final int ITEM_TYPE_OTHER = 1;

    public final int mineResId;
    public final int otherResId;

    @Inject DiscussionCache discussionCache;
    @Inject CurrentUserId currentUserId;
    @Inject DiscussionDTOFactory discussionDTOFactory;
    private MessageHeaderDTO messageHeaderDTO;

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

    public void setMessageHeaderDTO(MessageHeaderDTO messageHeaderDTO)
    {
        if (items != null && this.messageHeaderDTO != null)
        {
            items.remove(0);
        }
        this.messageHeaderDTO = messageHeaderDTO;
        if (messageHeaderDTO != null)
        {
            if (items == null)
            {
                items = new ArrayList<>();
            }
            DiscussionDTO fakeFirstDiscussion = discussionDTOFactory.createFrom(messageHeaderDTO);
            DiscussionKey fakeFirstDiscussionKey = fakeFirstDiscussion.getDiscussionKey();
            discussionCache.put(fakeFirstDiscussionKey, fakeFirstDiscussion);
            items.add(0, fakeFirstDiscussionKey);
        }
        notifyDataSetChanged();
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return isMine(discussionCache.get((DiscussionKey) getItem(position))) ? ITEM_TYPE_MINE : ITEM_TYPE_OTHER;
    }

    protected int getItemViewResId(int position)
    {
        return isMine(discussionCache.get((DiscussionKey) getItem(position))) ? mineResId : otherResId;
    }

    protected boolean isMine(AbstractDiscussionDTO discussionDTO)
    {
        return discussionDTO != null && currentUserId.toUserBaseKey().key.equals(discussionDTO.userId);
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
