package com.androidth.general.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.i18n.LanguageDTO;

public class LanguageChineseSimplifiedDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageChineseSimplifiedDTO(@NonNull Resources resources)
    {
        super("zh-CHS",
                resources.getString(R.string.translation_language_known_zh_CHS),
                resources.getString(R.string.translation_language_known_zh_CHS_own));
    }
    //</editor-fold>
}
