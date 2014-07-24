package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.competition.key.ProviderDisplayCellId;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class ProviderDisplayCellIdList extends DTOKeyIdList<ProviderDisplayCellId>
{
    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public ProviderDisplayCellIdList(@NotNull Date expirationDate)
    {
        super();
        this.expirationDate = expirationDate;
    }
    //</editor-fold>
}
