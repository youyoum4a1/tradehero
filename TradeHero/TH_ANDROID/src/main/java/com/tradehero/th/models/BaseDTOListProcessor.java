package com.ayondo.academy.models;

import android.support.annotation.NonNull;
import java.util.List;

public class BaseDTOListProcessor<
        DTOType,
        ListType extends List<? extends DTOType>>
    extends ThroughDTOProcessor<ListType>
{
    @NonNull private final DTOProcessor<DTOType> innerProcessor;

    //<editor-fold desc="Constructors">
    public BaseDTOListProcessor(@NonNull DTOProcessor<DTOType> innerProcessor)
    {
        this.innerProcessor = innerProcessor;
    }
    //</editor-fold>

    @Override public ListType process(ListType value)
    {
        for (DTOType item : value)
        {
            innerProcessor.process(item);
        }
        return value;
    }
}
