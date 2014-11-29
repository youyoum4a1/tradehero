package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;

public class LanguageKlingonDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageKlingonDTO(@NonNull Resources resources)
    {
        super("tlh",
                resources.getString(R.string.translation_language_known_tlh),
                resources.getString(R.string.translation_language_known_tlh_own));
    }
    //</editor-fold>
}
