package com.androidth.general.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.i18n.LanguageDTO;

public class LanguageNorwegianDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageNorwegianDTO(@NonNull Resources resources)
    {
        super("no",
                resources.getString(R.string.translation_language_known_no),
                resources.getString(R.string.translation_language_known_no_own));
    }
    //</editor-fold>
}
