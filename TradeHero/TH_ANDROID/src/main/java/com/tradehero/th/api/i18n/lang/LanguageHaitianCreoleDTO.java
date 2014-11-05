package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import android.support.annotation.NonNull;

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
