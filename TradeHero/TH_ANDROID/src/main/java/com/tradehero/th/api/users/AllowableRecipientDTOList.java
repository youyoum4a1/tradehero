package com.ayondo.academy.api.users;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.Collection;

public class AllowableRecipientDTOList extends BaseArrayList<AllowableRecipientDTO>
    implements DTO
{
    public AllowableRecipientDTOList(Collection<? extends AllowableRecipientDTO> c)
    {
        super(c);
    }
}
