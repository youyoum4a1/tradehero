package com.tradehero.th.api.i18n;

import java.util.HashMap;
import android.support.annotation.NonNull;

public class LanguageDTOMap extends HashMap<String, LanguageDTO>
{
    //<editor-fold desc="Constructors">
    public LanguageDTOMap()
    {
        super();
    }
    //</editor-fold>

    public void add(@NonNull LanguageDTO languageDTO)
    {
        put(languageDTO.code, languageDTO);
    }
}
