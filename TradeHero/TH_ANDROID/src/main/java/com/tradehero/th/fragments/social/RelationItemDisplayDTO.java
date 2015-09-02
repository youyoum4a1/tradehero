package com.tradehero.th.fragments.social;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;

public class RelationItemDisplayDTO implements DTO
{
    public final int orderFromServer;
    public final AllowableRecipientDTO allowableRecipientDTO;
    public final String displayName;
    public final String picture;

    public RelationItemDisplayDTO(int orderFromServer, AllowableRecipientDTO allowableRecipientDTO, String displayName, String picture)
    {
        this.orderFromServer = orderFromServer;
        this.allowableRecipientDTO = allowableRecipientDTO;
        this.displayName = displayName;
        this.picture = picture;
    }

    public static class DTOList<T extends RelationItemDisplayDTO> extends BaseArrayList<T> implements
            com.tradehero.common.persistence.DTO,
            ContainerDTO<T, DTOList<T>>
    {
        @Override public DTOList<T> getList()
        {
            return this;
        }
    }
}
