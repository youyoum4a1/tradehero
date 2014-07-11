package com.tradehero.th.api.translation.bing;

import android.content.Context;
import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.translation.TranslatableLanguageDTOFactory;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class BingLanguageDTOFactory extends TranslatableLanguageDTOFactory
{
    public static final int BING_LANGUAGE_CODES_RES_ID = R.array.bing_language_codes;

    @NotNull private final Resources resources;

    //<editor-fold desc="Constructors">
    @Inject public BingLanguageDTOFactory(@NotNull Context context)
    {
        super();
        this.resources = context.getResources();
    }
    //</editor-fold>

    @Override protected String[] getLanguageCodes()
    {
        return resources.getStringArray(BING_LANGUAGE_CODES_RES_ID);
    }
}
