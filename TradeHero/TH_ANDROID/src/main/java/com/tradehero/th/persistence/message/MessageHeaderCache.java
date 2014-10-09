package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MessageHeaderCache extends StraightDTOCacheNew<MessageHeaderId, MessageHeaderDTO>
{
    @NotNull private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderCache(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull public MessageHeaderDTO fetch(@NotNull MessageHeaderId key) throws Throwable
    {
        return messageServiceWrapper.getMessageHeader(key);
    }

    @NotNull public MessageHeaderDTOList put(@NotNull List<MessageHeaderDTO> messageHeaderDTOs)
    {
        MessageHeaderDTOList previous = new MessageHeaderDTOList();
        for (@NotNull MessageHeaderDTO messageHeaderDTO : messageHeaderDTOs)
        {
            previous.add(put(messageHeaderDTO.getDTOKey(), messageHeaderDTO));
        }
        return previous;
    }

    @NotNull public MessageHeaderDTOList get(@NotNull List<MessageHeaderId> messageHeaderIds)
    {
        MessageHeaderDTOList values = new MessageHeaderDTOList();
        for (@NotNull MessageHeaderId messageHeaderId : messageHeaderIds)
        {
            values.add(get(messageHeaderId));
        }
        return values;
    }

    public void setUnread(@NotNull MessageHeaderId messageHeaderId, boolean unread)
    {
        MessageHeaderDTO messageHeaderDTO = get(messageHeaderId);
        if (messageHeaderDTO != null && messageHeaderDTO.unread)
        {
            messageHeaderDTO.unread = unread;
            put(messageHeaderId, messageHeaderDTO);
        }
    }
}
