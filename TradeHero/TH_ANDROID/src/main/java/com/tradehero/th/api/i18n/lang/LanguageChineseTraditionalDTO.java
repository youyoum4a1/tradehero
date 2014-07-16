package com.tradehero.th.api.i18n.lang;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import org.jetbrains.annotations.NotNull;

public class LanguageChineseTraditionalDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageChineseTraditionalDTO(@NotNull Resources resources)
    {
        super("zh-CHT",
                resources.getString(R.string.translation_language_known_zh_CHT),
                resources.getString(R.string.translation_language_known_zh_CHT_own));
    }
    //</editor-fold>
}
