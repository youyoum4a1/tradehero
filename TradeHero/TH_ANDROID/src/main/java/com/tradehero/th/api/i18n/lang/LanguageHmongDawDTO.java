package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import android.support.annotation.NonNull;

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
