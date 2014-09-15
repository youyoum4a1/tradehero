package com.tradehero.th.fragments.discussion.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th2.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;

public class SecurityDiscussionCommentFragment extends AbstractDiscussionFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.security_discussion_comment, container, false);
        return view;
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // Nothing to do
    }
}
