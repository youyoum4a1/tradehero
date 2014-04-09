package com.tradehero.th.api.messages;

import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.KeyGenerator;
import java.util.Date;

/**
 * Created by wangliang on 14-4-4.
 */
public class MessageDTO implements DTO,KeyGenerator
{
    public int msgId;
    public String title;
    public String imageUrl;
    public String text;
    public Date createdAtUtc;

    public MessageDTO(int msgId, String imageUrl, String title, String text,
            Date createdAtUtc)
    {
        this.msgId = msgId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.text = text;
        this.createdAtUtc = createdAtUtc;
    }

    public MessageDTO()
    {
    }

    @Override public MessageKey getDTOKey()
    {
        return new MessageKey(msgId);
    }
}
