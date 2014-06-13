package com.tradehero.th.api.translation;

import com.tradehero.th.api.ExtendedDTO;

public class TranslationResult extends ExtendedDTO
{
    //<editor-fold desc="Constructors">
    public TranslationResult()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> TranslationResult(ExtendedDTOType other,
            Class<? extends ExtendedDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public String getContent()
    {
        return null;
    }

    public int logoResId()
    {
        return 0;
    }
}
