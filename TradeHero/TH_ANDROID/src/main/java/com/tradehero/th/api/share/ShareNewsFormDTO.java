package com.tradehero.th.api.share;

import com.tradehero.common.persistence.DTOKey;

public class ShareNewsFormDTO extends ShareFormDTO
{
    public ShareNewsFormDTO(DTOKey dtoKey)
    {
        super(ShareType.NEWS, dtoKey);
    }
}
