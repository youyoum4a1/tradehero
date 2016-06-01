package com.ayondo.academy.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.i18n.LanguageDTO;

public class LanguageKlingonQaakDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageKlingonQaakDTO(@NonNull Resources resources)
    {
        super("tlh-Qaak",
                resources.getString(R.string.translation_language_known_tlh_qaak),
                resources.getString(R.string.translation_language_known_tlh_qaak_own));
    }
    //</editor-fold>
}
