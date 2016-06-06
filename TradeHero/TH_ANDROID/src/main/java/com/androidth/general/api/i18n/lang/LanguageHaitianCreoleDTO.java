package com.androidth.general.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.i18n.LanguageDTO;

public class LanguageHaitianCreoleDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageHaitianCreoleDTO(@NonNull Resources resources)
    {
        super("ht",
                resources.getString(R.string.translation_language_known_ht),
                resources.getString(R.string.translation_language_known_ht_own));
    }
    //</editor-fold>
}
