package com.tradehero.th.fragments.onboarding.stock;

import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import org.jetbrains.annotations.NotNull;

public class SelectableSecurityDTO extends SelectableDTO<SecurityCompactDTO>
{
    //<editor-fold desc="Constructors">
    public SelectableSecurityDTO(@NotNull SecurityCompactDTO value)
    {
        super(value);
    }
    //</editor-fold>
}
