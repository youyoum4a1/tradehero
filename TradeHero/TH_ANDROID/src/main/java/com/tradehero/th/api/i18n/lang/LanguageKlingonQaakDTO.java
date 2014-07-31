package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import org.jetbrains.annotations.NotNull;

public class LanguageKlingonQaakDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageKlingonQaakDTO(@NotNull Resources resources)
    {
        super("tlh-Qaak",
                resources.getString(R.string.translation_language_known_tlh_qaak),
                resources.getString(R.string.translation_language_known_tlh_qaak_own));
    }
    //</editor-fold>
}
