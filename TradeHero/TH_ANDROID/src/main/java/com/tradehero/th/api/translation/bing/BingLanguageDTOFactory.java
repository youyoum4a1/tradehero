package com.tradehero.th.api.translation.bing;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTOFactory;
import com.tradehero.th.api.translation.TranslatableLanguageDTOFactory;
import javax.inject.Inject;

public class BingLanguageDTOFactory extends TranslatableLanguageDTOFactory
{
    public static final int BING_LANGUAGE_CODES_RES_ID = R.array.bing_language_codes;

    @NonNull private final Resources resources;

    //<editor-fold desc="Constructors">
    @Inject public BingLanguageDTOFactory(
            @NonNull Context context,
            @NonNull LanguageDTOFactory languageDTOFactory)
    {
        super(languageDTOFactory);
        this.resources = context.getResources();
    }
    //</editor-fold>

    @Override protected String[] getLanguageCodes()
    {
        return resources.getStringArray(BING_LANGUAGE_CODES_RES_ID);
    }
}
