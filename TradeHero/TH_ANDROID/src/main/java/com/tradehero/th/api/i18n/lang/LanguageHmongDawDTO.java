package com.ayondo.academy.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.i18n.LanguageDTO;

public class LanguageHmongDawDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageHmongDawDTO(@NonNull Resources resources)
    {
        super("mww",
                resources.getString(R.string.translation_language_known_mww),
                resources.getString(R.string.translation_language_known_mww_own));
    }
    //</editor-fold>
}
