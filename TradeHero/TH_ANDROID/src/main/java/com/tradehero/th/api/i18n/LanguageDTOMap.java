package com.tradehero.th.api.i18n;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

public class LanguageDTOMap extends HashMap<String, LanguageDTO>
{
    //<editor-fold desc="Constructors">
    public LanguageDTOMap()
    {
        super();
    }
    //</editor-fold>

    public void add(@NotNull LanguageDTO languageDTO)
    {
        put(languageDTO.code, languageDTO);
    }
}
