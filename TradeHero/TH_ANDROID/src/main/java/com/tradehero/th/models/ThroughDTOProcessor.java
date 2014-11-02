package com.tradehero.th.models;

import rx.functions.Action1;

public class ThroughDTOProcessor<DTOType> implements DTOProcessor<DTOType>, Action1<DTOType>
{
    @Override public DTOType process(DTOType value)
    {
        return value;
    }

    @Override public void call(DTOType dtoType)
    {
        process(dtoType);
    }
}
