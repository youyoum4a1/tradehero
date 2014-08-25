package com.tradehero.common.api;

import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class SelectableDTO<DTOType extends DTO>
    implements DTO
{
    @NotNull public final DTOType value;
    public boolean selected = false;

    //<editor-fold desc="Constructors">
    public SelectableDTO(@NotNull DTOType value)
    {
        this.value = value;
    }
    //</editor-fold>
}
