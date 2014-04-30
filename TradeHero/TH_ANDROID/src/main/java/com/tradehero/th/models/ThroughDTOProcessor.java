package com.tradehero.th.models;

public class ThroughDTOProcessor<DTOType> implements DTOProcessor<DTOType>
{
    @Override public DTOType process(DTOType value)
    {
        return value;
    }
}
