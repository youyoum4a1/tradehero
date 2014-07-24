package com.tradehero.th.fragments.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
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
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class UserTranslationSettingsViewHolder
{
    @Inject LanguageDTOFactory languageDTOFactory;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    @Inject TranslationTokenCache translationTokenCache;
    private DTOCacheNew.Listener<TranslationTokenKey, TranslationToken> translationTokenListener;
    protected UserTranslationSettingDTO userTranslationSettingDTO;

    protected DashboardPreferenceFragment preferenceFragment;
    protected PreferenceCategory translationContainer;
    protected Preference translationPreferredLang;
    protected CheckBoxPreference translationAuto;

    public void initViews(DashboardPreferenceFragment preferenceFragment)
    {
        DaggerUtils.inject(this);
        this.preferenceFragment = preferenceFragment;
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

    public void destroyViews()
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
        preferenceFragment = null;
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
            translationContainer.setEnabled(true);
            linkWith(languageDTOFactory.createFromCode(userTranslationSettingDTO.languageCode));
            translationAuto.setChecked(userTranslationSettingDTO.autoTranslate);
            translationAuto.setSummary(userTranslationSettingDTO.getProviderStringResId());
        }
    }

    protected void linkWith(@NotNull LanguageDTO languageDTO)
    {
        String lang = preferenceFragment.getString(
                R.string.translation_preferred_language_summary,
                languageDTO.code,
                languageDTO.name,
                languageDTO.nameInOwnLang);
        translationPreferredLang.setSummary(lang);
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
        preferenceFragment.getNavigator().pushFragment(TranslatableLanguageListFragment.class);
    }
}
