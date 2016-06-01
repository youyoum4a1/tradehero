package com.ayondo.academy.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.i18n.LanguageDTO;

public class LanguageHebrewDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageHebrewDTO(@NonNull Resources resources)
    {
        super("he",
                resources.getString(R.string.translation_language_known_he),
                resources.getString(R.string.translation_language_known_he_own));
    }
    //</editor-fold>
}
