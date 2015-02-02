package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import com.squareup.picasso.RequestCreator;
import com.tradehero.th.api.discussion.DiscussionDTO;

public class DiscussionItemViewHolder<DiscussionDTOType extends DiscussionDTO>
    extends AbstractDiscussionItemViewHolder<DiscussionDTOType>
{
    //<editor-fold desc="Constructors">
    public DiscussionItemViewHolder(@NonNull Context context)
    {
        super(context);
    }
    //</editor-fold>

    @Override public void linkWith(DiscussionDTOType discussionDTO)
    {
        super.linkWith(discussionDTO);
        displayUser();
    }

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayUser();
    }

    @Override protected String getUserDisplayName()
    {
        if (discussionDTO != null && discussionDTO.user != null)
        {
            return discussionDTO.user.displayName;
        }
        return null;
    }

    @NonNull @Override protected RequestCreator createUserPicassoRequest()
    {
        if (discussionDTO != null && discussionDTO.user != null && discussionDTO.user.picture != null)
        {
            return picasso.load(discussionDTO.user.picture);
        }
        return super.createUserPicassoRequest();
    }
    //</editor-fold>
}
