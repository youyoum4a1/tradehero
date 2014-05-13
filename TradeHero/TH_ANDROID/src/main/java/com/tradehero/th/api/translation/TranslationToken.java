package com.tradehero.th.api.translation;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.ExtendedDTO;

public class TranslationToken extends ExtendedDTO implements DTO
{
    public String type;

    //<editor-fold desc="Constructors">
    public TranslationToken()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> TranslationToken(ExtendedDTOType other,
            Class<? extends ExtendedDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public boolean isValid()
    {
        throw new IllegalStateException("Needs to be implemented");
    }
}
