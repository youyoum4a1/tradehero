package com.tradehero.th.api.translation.bing;

import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import org.jetbrains.annotations.NotNull;

public class BingUserTranslationSettingDTO extends UserTranslationSettingDTO
{
    public static final String SETTING_TYPE = "MicrosoftTranslator";

    //<editor-fold desc="Constructors">
    private BingUserTranslationSettingDTO()
    {
        // This constructor is necessary for Json deserialisation.
        super();
    }

    public BingUserTranslationSettingDTO(
            @NotNull String languageCode)
    {
        super(languageCode);
    }

    public BingUserTranslationSettingDTO(
            @NotNull String languageCode,
            boolean autoTranslate)
    {
        super(languageCode, autoTranslate);
    }
    //</editor-fold>

    @NotNull @Override public BingUserTranslationSettingDTO cloneForLanguage(@NotNull LanguageDTO languageDTO)
    {
        return new BingUserTranslationSettingDTO(languageDTO.code, autoTranslate);
    }
}
