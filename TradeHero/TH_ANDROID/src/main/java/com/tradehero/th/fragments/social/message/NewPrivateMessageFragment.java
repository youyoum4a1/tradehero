package com.tradehero.th.fragments.social.message;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.message.MessageThreadHeaderCache;
import javax.inject.Inject;
import retrofit.RetrofitError;
import timber.log.Timber;

public class NewPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    protected boolean isFresh = true;

    @Inject protected MessageThreadHeaderCache messageThreadHeaderCache;
    protected DTOCache.GetOrFetchTask<UserBaseKey, MessageHeaderDTO> messageThreadHeaderFetchTask;

    @Override public void onResume()
    {
        super.onResume();
        fetchMessageThreadHeader();
    }

    @Override public void onDestroyView()
    {
        detachMessageThreadHeaderFetchTask();
        super.onDestroyView();
    }

    protected void detachMessageThreadHeaderFetchTask()
    {
        if (messageThreadHeaderFetchTask != null)
        {
            messageThreadHeaderFetchTask.setListener(null);
        }
        messageThreadHeaderFetchTask = null;
    }

    protected void fetchMessageThreadHeader()
    {
        detachMessageThreadHeaderFetchTask();
        messageThreadHeaderFetchTask = messageThreadHeaderCache.getOrFetch(correspondentId,
                createMessageThreadHeaderCacheListener());
        messageThreadHeaderFetchTask.execute();
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        super.handleCommentPosted(discussionDTO);
        isFresh = false;
        if (getDiscussionKey() == null)
        {
            // We do this in order to ensure the next message is not a new one.
            linkWith(discussionDTO.getDiscussionKey(), true);
        }
    }

    protected DTOCache.Listener<UserBaseKey, MessageHeaderDTO> createMessageThreadHeaderCacheListener()
    {
        return new NewPrivateMessageFragmentThreadHeaderCacheListener();
    }

    protected class NewPrivateMessageFragmentThreadHeaderCacheListener
            implements DTOCache.Listener<UserBaseKey, MessageHeaderDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, MessageHeaderDTO value,
                boolean fromCache)
        {
            if (getDiscussionKey() == null)
            {
                if (discussionKeyFactory == null)
                {
                    Timber.e(new NullPointerException("DiscussionKeyFactory null"), null);
                }
                linkWith(discussionKeyFactory.create(value), true);
            }
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            if (!(error instanceof RetrofitError) ||
                    ((RetrofitError) error).getResponse().getStatus() != 404)
            {
                THToast.show(R.string.error_fetch_message_thread_header);
                Timber.e(error, "Error while getting message thread");
            }
            else
            {
                // There is just no existing thread
            }
        }
    }
}
