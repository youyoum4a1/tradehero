package com.androidth.general.models;

import rx.functions.Func1;

public class ThroughDTOProcessor<DTOType> implements DTOProcessor<DTOType>,
        Func1<DTOType, DTOType>
{
    @Override public DTOType process(DTOType value)
    {
        return value;
    }

    @Override public DTOType call(DTOType dtoType)
    {
        return process(dtoType);
    }
}
