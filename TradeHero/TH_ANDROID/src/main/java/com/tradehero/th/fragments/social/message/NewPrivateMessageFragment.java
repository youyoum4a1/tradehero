package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOCacheNew;
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
    protected DTOCacheNew.Listener<UserBaseKey, MessageHeaderDTO> messageThreadHeaderFetchListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messageThreadHeaderFetchListener = createMessageThreadHeaderCacheListener();
    }

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

    @Override public void onDestroy()
    {
        messageThreadHeaderFetchListener = null;
        super.onDestroy();
    }

    protected void detachMessageThreadHeaderFetchTask()
    {
        messageThreadHeaderCache.unregister(messageThreadHeaderFetchListener);
    }

    protected void fetchMessageThreadHeader()
    {
        detachMessageThreadHeaderFetchTask();
        messageThreadHeaderCache.register(correspondentId, messageThreadHeaderFetchListener);
        messageThreadHeaderCache.getOrFetchAsync(correspondentId);
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

    protected DTOCacheNew.Listener<UserBaseKey, MessageHeaderDTO> createMessageThreadHeaderCacheListener()
    {
        return new NewPrivateMessageFragmentThreadHeaderCacheListener();
    }

    protected class NewPrivateMessageFragmentThreadHeaderCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, MessageHeaderDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, MessageHeaderDTO value)
        {
            if (getDiscussionKey() == null)
            {
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
