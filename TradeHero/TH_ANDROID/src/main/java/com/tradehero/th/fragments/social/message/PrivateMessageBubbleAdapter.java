package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by xavier2 on 2014/4/10.
 */
public class PrivateMessageBubbleAdapter extends ArrayAdapter<PrivateDiscussionDTO>
{
    public static final int ITEM_TYPE_MINE = 0;
    public static final int ITEM_TYPE_OTHER = 1;
    public static final int BUBBLE_LAYOUT_RES_ID_MINE = R.layout.private_message_bubble_mine;
    public static final int BUBBLE_LAYOUT_RES_ID_OTHER = R.layout.private_message_bubble_other;

    private LayoutInflater mInflater;
    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public PrivateMessageBubbleAdapter(Context context, List<PrivateDiscussionDTO> objects)
    {
        super(context, 0);
        addAll(objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getCount()
    {
        int count = super.getCount();
        return count;
    }

    @Override public long getItemId(int position)
    {
        long id = getItem(position).id;
        return id;
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        int viewType = iSentIt(getItem(position)) ? ITEM_TYPE_MINE : ITEM_TYPE_OTHER;
        return viewType;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        PrivateDiscussionDTO discussionDTO = getItem(position);
        if (convertView == null)
        {
            convertView = mInflater.inflate(getLayoutResId(position), null);
        }
        ((PrivateMessageBubbleView) convertView).display(discussionDTO);
        return convertView;
    }

    protected boolean iSentIt(AbstractDiscussionDTO discussionDTO)
    {
        return discussionDTO != null && currentUserId.toUserBaseKey().key.equals(discussionDTO.userId);
    }

    protected int getLayoutResId(int position)
    {
        int itemType = getItemViewType(position);
        switch (itemType)
        {
            case ITEM_TYPE_MINE:
                return BUBBLE_LAYOUT_RES_ID_MINE;
            case ITEM_TYPE_OTHER:
                return BUBBLE_LAYOUT_RES_ID_OTHER;
            default:
                throw new IllegalArgumentException("Unhandled itemViewType " + getItemViewType(position));
        }
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }
}
