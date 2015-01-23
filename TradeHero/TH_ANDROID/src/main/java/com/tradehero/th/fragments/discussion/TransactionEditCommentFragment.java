package com.tradehero.th.fragments.discussion;

import android.text.Editable;

public class TransactionEditCommentFragment extends SecurityDiscussionEditPostFragment
{
    private Editable unSpanedComment;

    @Override protected void initView()
    {
        super.initView();

        discussionPostActionButtonsView.hideSocialButtons();
    }

    public Editable getComment()
    {
        return unSpanedComment;
    }

    @Override protected void postDiscussion()
    {
        unSpanedComment = unSpanText(discussionPostContent.getText());
        getDashboardNavigator().popFragment();
    }
}
