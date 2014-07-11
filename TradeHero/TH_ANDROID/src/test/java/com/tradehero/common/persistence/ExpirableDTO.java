package com.tradehero.common.persistence;

public class ExpirableDTO extends BaseHasExpiration
    implements DTO
{
    //<editor-fold desc="Constructors">
    public ExpirableDTO()
    {
        super(-1);
    }

    public ExpirableDTO(int seconds)
    {
        super(seconds);
    }
    //</editor-fold>
}
