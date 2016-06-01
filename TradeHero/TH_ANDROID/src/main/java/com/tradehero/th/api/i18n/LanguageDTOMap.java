package com.ayondo.academy.api.i18n;

import android.support.annotation.NonNull;
import java.util.HashMap;

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
