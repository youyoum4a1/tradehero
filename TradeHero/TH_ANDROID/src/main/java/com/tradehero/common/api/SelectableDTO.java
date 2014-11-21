package com.tradehero.common.api;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

public class SelectableDTO<DTOType extends DTO>
    implements DTO
{
    @NonNull public final DTOType value;
    public boolean selected = false;

    //<editor-fold desc="Constructors">
    public SelectableDTO(@NonNull DTOType value)
    {
        this.value = value;
    }
    //</editor-fold>
}
