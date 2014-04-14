package com.tradehero.th.fragments.discussion.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;

/**
 * Created by thonguyen on 12/4/14.
 */
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

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
