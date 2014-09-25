package com.tradehero.th.fragments.discussion;

import android.view.View;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.RequestCreator;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;

public class DiscussionItemViewHolder<DiscussionDTOType extends DiscussionDTO>
    extends AbstractDiscussionItemViewHolder<DiscussionDTOType>
{
    //<editor-fold desc="Constructors">
    public DiscussionItemViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void linkWith(DiscussionDTOType discussionDTO, boolean andDisplay)
    {
        super.linkWith(discussionDTO, andDisplay);

        if (andDisplay)
        {
            displayUser();
        }
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

    @Override protected RequestCreator createUserPicassoRequest()
    {
        if (discussionDTO != null && discussionDTO.user != null && discussionDTO.user.picture != null)
        {
            return picasso.load(discussionDTO.user.picture);
        }
        return super.createUserPicassoRequest();
    }
    //</editor-fold>

    @Optional @OnClick({R.id.discussion_user_picture, R.id.user_profile_name})
    protected void handleUserClicked(View view)
    {
        if (discussionDTO != null)
        {
            notifyUserClicked(discussionDTO.getSenderKey());
        }
    }

    public static interface OnMenuClickedListener extends AbstractDiscussionItemViewHolder.OnMenuClickedListener
    {
        // Nothing yet
    }
}
