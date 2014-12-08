package com.tradehero.th.fragments.discussion;

import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionView;
import dagger.Component;

@Component
public interface FragmentDiscussionComponent
{
    void injectAbstractDiscussionCompactItemViewLinear(AbstractDiscussionCompactItemViewLinear target);
    void injectDiscussionItemViewLinear(DiscussionItemViewLinear target);
    void injectAbstractDiscussionCompactItemViewHolder(AbstractDiscussionCompactItemViewHolder target);
    void injectAbstractDiscussionItemViewHolder(AbstractDiscussionItemViewHolder target);
    void injectDiscussionItemViewHolder(DiscussionItemViewHolder target);
    void injectTimelineItemViewHolder(TimelineItemViewHolder target);
    void injectAbstractDiscussionFragment(AbstractDiscussionFragment target);

    void injectSecurityDiscussionFragment(SecurityDiscussionFragment target);
    void injectSecurityDiscussionView(SecurityDiscussionView target);
    void injectSecurityDiscussionItemViewLinear(SecurityDiscussionItemViewLinear target);
    void injectSecurityDiscussionCommentFragment(SecurityDiscussionCommentFragment target);

    void injectDiscussionView(DiscussionView target);
    void injectPostCommentView(PostCommentView target);
    void injectNewsDiscussionView(NewsDiscussionView target);

    void injectDiscussionEditPostFragment(DiscussionEditPostFragment target);
    void injectSecurityDiscussionEditPostFragment(SecurityDiscussionEditPostFragment target);
    void injectDiscussionPostActionButtonsView(DiscussionPostActionButtonsView target);
    void injectTransactionEditCommentFragment(TransactionEditCommentFragment target);
    void injectMentionActionButtonsView(MentionActionButtonsView target);

    void injectTimelineDiscussionFragment(TimelineDiscussionFragment target);
    void injectNewsDiscussionFragment(NewsDiscussionFragment target);
    void injectCommentItemViewLinear(CommentItemViewLinear target);
}
