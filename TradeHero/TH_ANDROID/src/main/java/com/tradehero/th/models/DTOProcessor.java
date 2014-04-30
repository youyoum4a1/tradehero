package com.tradehero.th.models;

public interface DTOProcessor<DTOType>
{
    DTOType process(DTOType value);
}
