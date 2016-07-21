package com.androidth.general.fragments.discussion;

import com.androidth.general.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.androidth.general.fragments.discussion.stock.SecurityDiscussionFragment;
import com.androidth.general.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import dagger.Module;

@Module(
        injects = {
                AbstractDiscussionCompactItemViewLinear.class,
                DiscussionItemViewLinear.class,
                AbstractDiscussionCompactItemViewHolder.class,
                AbstractDiscussionItemViewHolder.class,
                DiscussionItemViewHolder.class,
                TimelineItemViewHolder.class,
                AbstractDiscussionFragment.class,

                SecurityDiscussionFragment.class,
                SecurityDiscussionItemViewLinear.class,
                SecurityDiscussionCommentFragment.class,

                PostCommentView.class,

                DiscussionEditPostFragment.class,
                SecurityDiscussionEditPostFragment.class,
                DiscussionPostActionButtonsView.class,
                TransactionEditCommentFragment.class,
                MentionActionButtonsView.class,

                TimelineDiscussionFragment.class,
                NewsDiscussionFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentDiscussionModule
{
}
