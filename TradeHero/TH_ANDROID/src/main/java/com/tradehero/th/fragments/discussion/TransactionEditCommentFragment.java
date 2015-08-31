package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.utils.EditableUtil;
import com.tradehero.th.R;
import javax.inject.Inject;

public class TransactionEditCommentFragment extends SecurityDiscussionEditPostFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private Editable unSpannedComment;

    @Override protected void initView()
    {
        super.initView();

        discussionPostActionButtonsView.hideSocialButtons();

        Bundle bundle = getArguments();
        String comment = SecurityDiscussionEditPostFragment.getComment(bundle);

        if (comment != null)
        {
            discussionPostContent.setText(comment);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        postMenuButton.setTitle(R.string.done);
        setActionBarTitle(R.string.trade_comment);
    }

    public Editable getComment()
    {
        return unSpannedComment;
    }

    @Override protected void postDiscussion()
    {
        unSpannedComment = EditableUtil.unSpanText(discussionPostContent.getText());
        navigator.get().popFragment();
    }
}
