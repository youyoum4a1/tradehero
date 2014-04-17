package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.view.View;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import javax.inject.Inject;

public class ReplyPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    private static final String INITIATING_MESSAGE_HEADER_ID_BUNDLE_KEY = ReplyPrivateMessageFragment.class.getName() + ".initiatingMessageHeaderId";

    protected MessageHeaderId initiatingMessageHeaderId;
    protected MessageHeaderDTO initiatingMessageHeader;
    @Inject MessageHeaderCache messageHeaderCache;

    public static void putInitiatingMessageHeaderId(Bundle args, MessageHeaderId initiatingMessageHeaderId)
    {
        args.putBundle(INITIATING_MESSAGE_HEADER_ID_BUNDLE_KEY, initiatingMessageHeaderId.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initiatingMessageHeaderId = new MessageHeaderId(getArguments().getBundle(INITIATING_MESSAGE_HEADER_ID_BUNDLE_KEY));
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        ((PrivateDiscussionView) discussionView).setMessageHeaderId(initiatingMessageHeaderId);
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(messageHeaderCache.get(initiatingMessageHeaderId), true);
    }

    public void linkWith(MessageHeaderDTO messageHeaderDTO, boolean andDisplay)
    {
        this.initiatingMessageHeader = messageHeaderDTO;
        if (andDisplay)
        {
            // Anything to do?
        }
    }
}
