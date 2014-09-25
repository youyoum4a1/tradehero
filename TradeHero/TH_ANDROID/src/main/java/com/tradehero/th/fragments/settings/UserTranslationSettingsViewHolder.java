package com.tradehero.th.fragments.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.v4.preference.PreferenceFragment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.i18n.LanguageDTOFactory;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.fragments.translation.TranslatableLanguageListFragment;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class UserTranslationSettingsViewHolder extends BaseSettingViewHolder
{
    @NotNull private final LanguageDTOFactory languageDTOFactory;
    @NotNull private final UserTranslationSettingPreference userTranslationSettingPreference;
    @NotNull private final TranslationTokenCache translationTokenCache;
    @Nullable private DTOCacheNew.Listener<TranslationTokenKey, TranslationToken> translationTokenListener;
    @Nullable protected UserTranslationSettingDTO userTranslationSettingDTO;

    @Nullable protected PreferenceCategory translationContainer;
    @Nullable protected Preference translationPreferredLang;
    @Nullable protected CheckBoxPreference translationAuto;

    //<editor-fold desc="Constructors">
    @Inject public UserTranslationSettingsViewHolder(
            @NotNull LanguageDTOFactory languageDTOFactory,
            @NotNull UserTranslationSettingPreference userTranslationSettingPreference,
            @NotNull TranslationTokenCache translationTokenCache)
    {
        this.languageDTOFactory = languageDTOFactory;
        this.userTranslationSettingPreference = userTranslationSettingPreference;
        this.translationTokenCache = translationTokenCache;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        translationTokenListener = createTranslationTokenListener();

        translationContainer = (PreferenceCategory) preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_translations_container));
        translationContainer.setEnabled(false);

        translationPreferredLang =
                preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_translations_preferred_language));
        if (translationPreferredLang != null)
        {
            translationPreferredLang.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handlePreferredLanguageClicked();
                    return true;
                }
            });
        }

        translationAuto = (CheckBoxPreference) preferenceFragment.findPreference(
                preferenceFragment.getString(R.string.key_settings_translations_auto_translate));
        if (translationAuto != null)
        {
            translationAuto.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    handleAutoTranslateClicked((boolean) newValue);
                    return true;
                }
            });
        }
        fetchTranslationToken();
    }

    @Override public void destroyViews()
    {
        detachTranslationTokenCache();

        if (translationAuto != null)
        {
            translationAuto.setOnPreferenceChangeListener(null);
        }
        translationAuto = null;
        if (translationPreferredLang != null)
        {
            translationPreferredLang.setOnPreferenceClickListener(null);
        }
        translationPreferredLang = null;
        translationContainer = null;
        translationTokenListener = null;
        super.destroyViews();
    }

    protected void fetchTranslationToken()
    {
        detachTranslationTokenCache();
        TranslationTokenKey key = new TranslationTokenKey();
        translationTokenCache.register(key, translationTokenListener);
        translationTokenCache.getOrFetchAsync(key);
    }

    protected void detachTranslationTokenCache()
    {
        translationTokenCache.unregister(translationTokenListener);
    }

    protected DTOCacheNew.Listener<TranslationTokenKey, TranslationToken> createTranslationTokenListener()
    {
        return new SettingsTranslationTokenListener();
    }

    protected class SettingsTranslationTokenListener implements DTOCacheNew.Listener<TranslationTokenKey, TranslationToken>
    {
        @Override public void onDTOReceived(@NotNull TranslationTokenKey key, @NotNull TranslationToken value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull TranslationTokenKey key, @NotNull Throwable error)
        {
            Timber.e("Failed", error);
        }
    }

    protected void linkWith(@NotNull TranslationToken token)
    {
        try
        {
            linkWith(userTranslationSettingPreference.getOfSameTypeOrDefault(token));
        }
        catch (IOException e)
        {
            Timber.e(e, "Failed to get translation setting");
        }
    }

    protected void linkWith(@Nullable UserTranslationSettingDTO userTranslationSettingDTO)
    {
        this.userTranslationSettingDTO = userTranslationSettingDTO;
        if (userTranslationSettingDTO != null)
        {
            //noinspection ConstantConditions
            translationContainer.setEnabled(true);
            linkWith(languageDTOFactory.createFromCode(userTranslationSettingDTO.languageCode));
            //noinspection ConstantConditions
            translationAuto.setChecked(userTranslationSettingDTO.autoTranslate);
            translationAuto.setSummary(userTranslationSettingDTO.getProviderStringResId());
        }
    }

    protected void linkWith(@NotNull LanguageDTO languageDTO)
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            String lang = preferenceFragmentCopy.getString(
                    R.string.translation_preferred_language_summary,
                    languageDTO.code,
                    languageDTO.name,
                    languageDTO.nameInOwnLang);
            Preference translationPreferredLangCopy = translationPreferredLang;
            if (translationPreferredLangCopy != null)
            {
                translationPreferredLangCopy.setSummary(lang);
            }
        }
    }

    protected void handleAutoTranslateClicked(boolean newValue)
    {
        if (userTranslationSettingDTO != null)
        {
            userTranslationSettingDTO = userTranslationSettingDTO.cloneForAuto(newValue);
            try
            {
                userTranslationSettingPreference.addOrReplaceSettingDTO(userTranslationSettingDTO);
            }
            catch (JsonProcessingException e)
            {
                THToast.show(R.string.translation_error_saving_preference);
                e.printStackTrace();
            }
        }
    }

    protected void handlePreferredLanguageClicked()
    {
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            preferenceFragmentCopy.getNavigator().pushFragment(TranslatableLanguageListFragment.class);
        }
    }

    @Override public boolean isUnread()
    {
        return (translationPreferredLang instanceof ShowUnreadPreference)
                && !((ShowUnreadPreference) translationPreferredLang).isVisited();
    }

    @Override public Preference getPreference()
    {
        return translationContainer;
    }
}
