package com.ayondo.academy.api.translation.bing;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.ayondo.academy.R;
import com.ayondo.academy.api.i18n.LanguageDTO;
import com.ayondo.academy.api.translation.UserTranslationSettingDTO;

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
            @NonNull String languageCode)
    {
        super(languageCode);
    }

    public BingUserTranslationSettingDTO(
            @NonNull String languageCode,
            boolean autoTranslate)
    {
        super(languageCode, autoTranslate);
    }
    //</editor-fold>

    @NonNull @Override public BingUserTranslationSettingDTO cloneForLanguage(@NonNull LanguageDTO languageDTO)
    {
        return new BingUserTranslationSettingDTO(languageDTO.code, autoTranslate);
    }

    @NonNull @Override public UserTranslationSettingDTO cloneForAuto(boolean newAutoValue)
    {
        return new BingUserTranslationSettingDTO(languageCode, newAutoValue);
    }

    @Override @StringRes public int getProviderStringResId()
    {
        return R.string.translation_provided_by_bing;
    }
}
