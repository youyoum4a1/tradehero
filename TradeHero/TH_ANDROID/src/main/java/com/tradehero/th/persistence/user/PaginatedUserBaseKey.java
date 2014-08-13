package com.tradehero.th.persistence.user;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PaginatedUserBaseKey extends PaginatedDTO<UserBaseKey>
{
    @NotNull public Date expirationDate;

    PaginatedUserBaseKey(
            @NotNull PaginatedAllowableRecipientDTO paginatedAllowableRecipientDTO,
            @NotNull AllowableRecipientCache allowableRecipientCache)
    {
        allowableRecipientCache.put(paginatedAllowableRecipientDTO.getData());
        List<UserBaseKey> keys = new ArrayList<>();
        for (AllowableRecipientDTO allowableRecipientDTO : paginatedAllowableRecipientDTO.getData())
        {
            keys.add(allowableRecipientDTO.user.getBaseKey());
        }
        this.setData(keys);
        this.setPagination(paginatedAllowableRecipientDTO.getPagination());
        this.expirationDate = paginatedAllowableRecipientDTO.expirationDate;
    }

    @Nullable PaginatedAllowableRecipientDTO inflate(@NotNull AllowableRecipientCache allowableRecipientCache)
    {
        BaseArrayList<AllowableRecipientDTO> cached = allowableRecipientCache.get(getData());
        if (cached.hasNullItem())
        {
            return null;
        }
        PaginatedAllowableRecipientDTO inflated = new PaginatedAllowableRecipientDTO(expirationDate);
        inflated.setPagination(getPagination());
        inflated.setData(cached);
        return inflated;
    }
}
