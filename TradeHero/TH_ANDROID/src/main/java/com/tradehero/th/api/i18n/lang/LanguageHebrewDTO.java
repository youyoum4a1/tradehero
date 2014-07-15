package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import org.jetbrains.annotations.NotNull;

public class LanguageHebrewDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageHebrewDTO(@NotNull Resources resources)
    {
        super("he",
                resources.getString(R.string.translation_language_known_he),
                resources.getString(R.string.translation_language_known_he_own));
    }
    //</editor-fold>
}
