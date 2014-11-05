package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class MessageHeaderCacheRx extends BaseFetchDTOCacheRx<MessageHeaderId, MessageHeaderDTO>
{
    @NotNull private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderCacheRx(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull MessageServiceWrapper messageServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), 5, 5, dtoCacheUtil);
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<MessageHeaderDTO> fetch(@NotNull MessageHeaderId key)
    {
        return messageServiceWrapper.getMessageHeaderRx(key);
    }

    public void onNext(@NotNull List<? extends MessageHeaderDTO> messageHeaderDTOs)
    {
        for (@NotNull MessageHeaderDTO messageHeaderDTO : messageHeaderDTOs)
        {
            onNext(messageHeaderDTO.getDTOKey(), messageHeaderDTO);
        }
    }

    public void setUnread(@NotNull MessageHeaderId messageHeaderId, boolean unread)
    {
        MessageHeaderDTO messageHeaderDTO = getValue(messageHeaderId);
        if (messageHeaderDTO != null && messageHeaderDTO.unread)
        {
            messageHeaderDTO.unread = unread;
            onNext(messageHeaderId, messageHeaderDTO);
        }
    }
}
