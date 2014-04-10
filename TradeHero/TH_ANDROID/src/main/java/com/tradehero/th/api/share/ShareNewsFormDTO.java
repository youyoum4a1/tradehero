package com.tradehero.th.api.share;

import com.tradehero.common.persistence.DTOKey;

/**
 * Created by alex on 14-4-8.
 */
public class ShareNewsFormDTO extends ShareFormDTO
{
    public ShareNewsFormDTO(DTOKey dtoKey)
    {
        super(ShareType.NEWS, dtoKey);
    }
}
