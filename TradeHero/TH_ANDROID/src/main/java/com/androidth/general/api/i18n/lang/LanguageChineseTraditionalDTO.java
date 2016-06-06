package com.androidth.general.api.i18n.lang;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.i18n.LanguageDTO;

public class LanguageChineseTraditionalDTO extends LanguageDTO
{
    //<editor-fold desc="Constructors">
    public LanguageChineseTraditionalDTO(@NonNull Resources resources)
    {
        super("zh-CHT",
                resources.getString(R.string.translation_language_known_zh_CHT),
                resources.getString(R.string.translation_language_known_zh_CHT_own));
    }
    //</editor-fold>
}
