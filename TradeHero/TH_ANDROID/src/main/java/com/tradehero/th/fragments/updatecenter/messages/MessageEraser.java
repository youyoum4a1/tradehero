package com.tradehero.th.fragments.updatecenter.messages;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by wangliang on 14-4-12.
 */
@Singleton
public class MessageEraser
{
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;

    public MessageEraser()
    {
        DaggerUtils.inject(this);
    }

    public void deleteMessage(MessageHeaderId messageHeaderId)
    {
        messageServiceWrapper.get().deleteMessage(messageHeaderId.key,new MessageCallback(messageHeaderId));
    }

    class MessageCallback implements Callback<Response>{

        private MessageHeaderId messageHeaderId;

        MessageCallback(MessageHeaderId messageHeaderId)
        {
            this.messageHeaderId = messageHeaderId;
        }

        @Override public void success(Response discussionDTO, Response response)
        {
            messageHeaderId.markDeleted = true;
            Timber.d("Delete message %s success",messageHeaderId);
        }

        @Override public void failure(RetrofitError error)
        {
            messageHeaderId.markDeleted = false;
            Timber.d("Delete message %s error",messageHeaderId);
        }
    }

}
