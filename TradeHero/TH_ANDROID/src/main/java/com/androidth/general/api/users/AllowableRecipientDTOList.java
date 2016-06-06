package com.androidth.general.api.users;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;
import java.util.Collection;

public class AllowableRecipientDTOList extends BaseArrayList<AllowableRecipientDTO>
    implements DTO
{
    public AllowableRecipientDTOList(Collection<? extends AllowableRecipientDTO> c)
    {
        super(c);
    }
}
