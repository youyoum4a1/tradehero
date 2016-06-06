package com.androidth.general.persistence.message;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.discussion.key.MessageHeaderId;
import com.androidth.general.network.service.MessageServiceWrapper;
import com.androidth.general.persistence.SingleCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class MessageHeaderCacheRx extends BaseFetchDTOCacheRx<MessageHeaderId, MessageHeaderDTO>
{
    @NonNull private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderCacheRx(
            @SingleCacheMaxSize IntPreference maxSize,
            @NonNull MessageServiceWrapper messageServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<MessageHeaderDTO> fetch(@NonNull MessageHeaderId key)
    {
        return messageServiceWrapper.getMessageHeaderRx(key);
    }

    public void onNext(@NonNull List<? extends MessageHeaderDTO> messageHeaderDTOs)
    {
        for (MessageHeaderDTO messageHeaderDTO : messageHeaderDTOs)
        {
            onNext(messageHeaderDTO.getDTOKey(), messageHeaderDTO);
        }
    }

    public void setUnread(@NonNull MessageHeaderId messageHeaderId, boolean unread)
    {
        MessageHeaderDTO messageHeaderDTO = getCachedValue(messageHeaderId);
        if (messageHeaderDTO != null && messageHeaderDTO.unread != unread)
        {
            messageHeaderDTO.unread = unread;
            onNext(messageHeaderId, messageHeaderDTO);
        }
    }

    public void setUnreadAll(boolean unread)
    {
        for (MessageHeaderId id : snapshot().keySet())
        {
            setUnread(id, unread);
        }
    }
}
