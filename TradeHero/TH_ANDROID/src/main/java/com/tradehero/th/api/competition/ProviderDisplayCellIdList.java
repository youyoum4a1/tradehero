package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.competition.key.ProviderDisplayCellId;
import java.util.Date;
import android.support.annotation.NonNull;

public class ProviderDisplayCellIdList extends DTOKeyIdList<ProviderDisplayCellId>
{
    @NonNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public ProviderDisplayCellIdList(@NonNull Date expirationDate)
    {
        super();
        this.expirationDate = expirationDate;
    }
    //</editor-fold>
}
