package com.ayondo.academy.fragments.onboarding.stock;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;

public class SelectableSecurityDTO extends SelectableDTO<SecurityCompactDTO>
{
    //<editor-fold desc="Constructors">
    public SelectableSecurityDTO(@NonNull SecurityCompactDTO value)
    {
        super(value);
    }

    public SelectableSecurityDTO(@NonNull SecurityCompactDTO value, boolean selected)
    {
        super(value, selected);
    }
    //</editor-fold>
}
