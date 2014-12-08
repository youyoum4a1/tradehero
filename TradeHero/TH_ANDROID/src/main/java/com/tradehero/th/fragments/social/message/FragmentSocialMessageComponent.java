package com.tradehero.th.fragments.social.message;

import dagger.Component;

@Component
public interface FragmentSocialMessageComponent
{
    void injectNewPrivateMessageFragment(NewPrivateMessageFragment target);
    void injectReplyPrivateMessageFragment(ReplyPrivateMessageFragment target);
    void injectPrivateDiscussionView(PrivateDiscussionView target);
    void injectPrivateMessageBubbleViewLinear(PrivateMessageBubbleViewLinear target);
    void injectAbstractPrivateMessageFragment(AbstractPrivateMessageFragment target);
    void injectPrivatePostCommentView(PrivatePostCommentView target);
}
