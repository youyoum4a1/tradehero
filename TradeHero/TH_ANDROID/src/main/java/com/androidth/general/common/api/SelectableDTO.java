package com.androidth.general.common.api;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;

public class SelectableDTO<DTOType extends DTO>
    implements DTO
{
    public static final boolean DEFAULT_SELECTED = false;

    @NonNull public final DTOType value;
    public boolean selected;

    //<editor-fold desc="Constructors">
    public SelectableDTO(@NonNull DTOType value)
    {
        this(value, DEFAULT_SELECTED);
    }

    public SelectableDTO(@NonNull DTOType value, boolean selected)
    {
        this.value = value;
        this.selected = selected;
    }
    //</editor-fold>
}
