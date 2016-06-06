package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import com.androidth.general.common.utils.EditableUtil;
import com.androidth.general.R;
import javax.inject.Inject;

public class TransactionEditCommentFragment extends SecurityDiscussionEditPostFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private Editable unSpannedComment;

    @Override protected void initView()
    {
        super.initView();

        discussionPostActionButtonsView.hideSocialButtons();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        String comment = SecurityDiscussionEditPostFragment.getComment(savedInstanceState != null ? savedInstanceState : getArguments());
        discussionPostContent.setText(comment);
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        SecurityDiscussionEditPostFragment.putComment(outState, discussionPostContent.getText().toString());
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
