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
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.location.LocationListFragment;
import com.tradehero.th.fragments.translation.TranslatableLanguageListFragment;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import com.tradehero.th.persistence.user.UserProfileCache;
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
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    private DTOCacheNew.Listener<TranslationTokenKey, TranslationToken> translationTokenListener;
    protected UserTranslationSettingDTO userTranslationSettingDTO;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    protected DashboardPreferenceFragment preferenceFragment;
    protected PreferenceCategory translationContainer;
    protected Preference translationPreferredLang;
    protected CheckBoxPreference translationAuto;
    protected Preference locationPreference;

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

        locationPreference =
                preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_location));
        if (locationPreference != null)
        {
            locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleLocationClicked();
                    return true;
                }
            });
        }
        fetchTranslationToken();

        userProfileCacheListener = new UserProfileCacheListener();
    }

    public void destroyViews()
    {
        detachTranslationTokenCache();
        userProfileCache.unregister(currentUserId.toUserBaseKey(), userProfileCacheListener);

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
        locationPreference = null;
        translationContainer = null;
        translationTokenListener = null;
        preferenceFragment = null;
        userProfileCacheListener = null;
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

        userProfileCache.unregister(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void updateLocation()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null && userProfileDTO.countryCode != null)
        {
            locationPreference.setSummary(userProfileDTO.countryCode);
            if (Country.valueOf(userProfileDTO.countryCode) != null)
            {
                locationPreference.setIcon(Country.valueOf(userProfileDTO.countryCode).logoId);
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
        preferenceFragment.getNavigator().pushFragment(TranslatableLanguageListFragment.class);
    }

    protected void handleLocationClicked()
    {
        preferenceFragment.getNavigator().pushFragment(LocationListFragment.class);
    }

    protected class UserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(final UserBaseKey key, final UserProfileDTO value)
        {
            updateLocation();
            userProfileCache.unregister(currentUserId.toUserBaseKey(), userProfileCacheListener);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile %s", key, error);
            userProfileCache.unregister(currentUserId.toUserBaseKey(), userProfileCacheListener);
        }
    }
}
