package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import android.support.annotation.NonNull;

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
